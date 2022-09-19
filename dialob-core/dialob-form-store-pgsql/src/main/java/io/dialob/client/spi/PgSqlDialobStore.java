package io.dialob.client.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.dialob.client.api.DialobStore;
import io.dialob.client.api.ImmutableStoreEntity;
import io.dialob.client.api.ImmutableStoreExceptionMsg;
import io.dialob.client.spi.PgSqlConfig.EntityState;
import io.dialob.client.spi.exceptions.StoreException;
import io.dialob.client.spi.support.BlobDeserializer;
import io.dialob.client.spi.support.DialobAssert;
import io.dialob.client.spi.support.DocumentQueryBuilder;
import io.dialob.client.spi.support.OidUtils;
import io.dialob.client.spi.support.PersistenceCommands;
import io.resys.thena.docdb.api.DocDB;
import io.resys.thena.docdb.api.actions.CommitActions.CommitStatus;
import io.resys.thena.docdb.api.actions.ObjectsActions.ObjectsStatus;
import io.resys.thena.docdb.api.actions.RepoActions.RepoStatus;
import io.resys.thena.docdb.spi.pgsql.DocDBFactory;
import io.smallrye.mutiny.Uni;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;

public class PgSqlDialobStore extends PersistenceCommands implements DialobStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(PgSqlDialobStore.class);

  public PgSqlDialobStore(PgSqlConfig config) {
    super(config);
  }
  @Override
  public String getRepoName() {
    return config.getRepoName();
  }
  @Override
  public String getHeadName() {
    return config.getHeadName();
  }
  @Override
  public StoreRepoBuilder repo() {
    return new StoreRepoBuilder() {
      private String repoName;
      private String headName;
      @Override
      public StoreRepoBuilder repoName(String repoName) {
        this.repoName = repoName;
        return this;
      }
      @Override
      public StoreRepoBuilder headName(String headName) {
        this.headName = headName;
        return this;
      }
      @Override
      public Uni<DialobStore> create() {
        DialobAssert.notNull(repoName, () -> "repoName must be defined!");
        final var client = config.getClient();
        final var newRepo = client.repo().create().name(repoName).build();
        return newRepo.onItem().transform((repoResult) -> {
          if(repoResult.getStatus() != RepoStatus.OK) {
            throw new StoreException("REPO_CREATE_FAIL", null, 
                ImmutableStoreExceptionMsg.builder()
                .id(repoResult.getStatus().toString())
                .value(repoName)
                .addAllArgs(repoResult.getMessages().stream().map(message->message.getText()).collect(Collectors.toList()))
                .build()); 
          }
          
          return build();
        });
      }
      @Override
      public DialobStore build() {
        DialobAssert.notNull(repoName, () -> "repoName must be defined!");
        return new PgSqlDialobStore(ImmutablePgSqlConfig.builder()
            .from(config)
            .repoName(repoName)
            .headName(headName == null ? config.getHeadName() : headName)
            .build());
      }
      @Override
      public Uni<Boolean> createIfNot() {
        final var client = config.getClient();
        
        return client.repo().query().id(config.getRepoName()).get().onItem().transformToUni(repo -> {
          if(repo == null) {
            return client.repo().create().name(config.getRepoName()).build().onItem().transform(newRepo -> true); 
          }
          return Uni.createFrom().item(true);
        });
      }
    };
  }
  
  @Override
  public QueryBuilder query() {
    return new DocumentQueryBuilder(config);
  }
  @Override
  public Uni<StoreEntity> create(CreateStoreEntity newType) {
    final var gid = gid(newType.getBodyType());
    final var entity = (StoreEntity) ImmutableStoreEntity.builder()
        .id(gid)
        .version(OidUtils.generateVersionOID())
        .hash("")
        .body(newType.getBody())
        .bodyType(newType.getBodyType())
        .build();
    return super.save(entity);
  }

  @Override
  public Uni<StoreEntity> update(UpdateStoreEntity updateType) {
    final Uni<EntityState> query = getEntityState(updateType.getId());
    return query.onItem().transformToUni(state -> {
      
      if(!state.getEntity().getVersion().equals(updateType.getVersion())) {
        throw new StoreException("VERSION_LOCKING_ERROR", null, ImmutableStoreExceptionMsg.builder()
            .id("VERSION_LOCKING_ERROR_ON_UPDATE")
            .value("Version check mismatch on update, you provided: " + updateType.getVersion() + ", store expecting: " + state.getEntity().getVersion() + "!")
            .args(Arrays.asList(state.getEntity().getVersion(), updateType.getVersion()))
            .build()); 
      }
      
      final StoreEntity entity = ImmutableStoreEntity.builder()
          .from(state.getEntity())
          .version(OidUtils.generateVersionOID())
          .id(updateType.getId())
          .bodyType(state.getEntity().getBodyType())
          .body(updateType.getBody())
          .build();
      return super.save(entity);
    });
  }
  @Override
  public Uni<List<StoreEntity>> batch(List<StoreCommand> batchType) {

    final var create = batchType.stream()
        .filter(e -> e instanceof CreateStoreEntity).map(e -> (CreateStoreEntity) e)
        .collect(Collectors.toList());
    final var update = batchType.stream()
        .filter(e -> e instanceof UpdateStoreEntity).map(e -> (UpdateStoreEntity) e)
        .collect(Collectors.toList());
    final var del = batchType.stream()
        .filter(e -> e instanceof DeleteStoreEntity).map(e -> (DeleteStoreEntity) e)
        .collect(Collectors.toList());
    
    final var commitBuilder = config.getClient().commit().head()
        .head(config.getRepoName(), config.getHeadName())
        .message(
            "Save batch with new: " + create.size() + 
            " , updated: " + update.size() +
            " and deleted: " + del.size() +
            " entries")
        .parentIsLatest()
        .author(config.getAuthorProvider().getAuthor());
    
    
    return get().onItem().transformToUni(currentState -> {
      
      final List<String> ids = new ArrayList<>();
      for(final var toBeSaved : create) {
        final var gid = gid(toBeSaved.getBodyType());
        final var entity = (StoreEntity) ImmutableStoreEntity.builder()
            .id(gid)
            .version(OidUtils.generateVersionOID())
            .hash("")
            .body(toBeSaved.getBody())
            .bodyType(toBeSaved.getBodyType())
            .build();
        commitBuilder.append(entity.getId(), config.getSerializer().toString(entity));
        ids.add(gid);
      }
      for(final var toBeSaved : update) {
        final var entity = (StoreEntity) ImmutableStoreEntity.builder()
            .id(toBeSaved.getId())
            .version(OidUtils.generateVersionOID())
            .hash("")
            .body(toBeSaved.getBody())
            .bodyType(toBeSaved.getBodyType())
            .build();
        
        final var currentValue = super.getEntityFromState(currentState, toBeSaved.getId());
        if(!currentValue.getVersion().equals(toBeSaved.getVersion())) {
          throw new StoreException("VERSION_LOCKING_ERROR", null, ImmutableStoreExceptionMsg.builder()
              .id("VERSION_LOCKING_ERROR_ON_UPDATE_IN_BATCH")
              .value("Version check mismatch on update in batch, you provided: " + toBeSaved.getVersion() + ", store expecting: " + currentValue.getVersion() + "!")
              .args(Arrays.asList(currentValue.getVersion(), toBeSaved.getVersion()))
              .build()); 
        }
        commitBuilder.append(entity.getId(), config.getSerializer().toString(entity));
        ids.add(entity.getId());
      }    
      
      for(final var toBeDeleted : del) {
        final var currentValue = super.getEntityFromState(currentState, toBeDeleted.getId());
        if(!currentValue.getVersion().equals(toBeDeleted.getVersion())) {
          throw new StoreException("VERSION_LOCKING_ERROR", null, ImmutableStoreExceptionMsg.builder()
              .id("VERSION_LOCKING_ERROR_ON_DELETE_IN_BATCH")
              .value("Version check mismatch on delete in batch, you provided: " + toBeDeleted.getVersion() + ", store expecting: " + currentValue.getVersion() + "!")
              .args(Arrays.asList(currentValue.getVersion(), toBeDeleted.getVersion()))
              .build()); 
        }
        commitBuilder.remove(toBeDeleted.getId());
      }
      
      return commitBuilder.build().onItem().transformToUni(commit -> {
        if(commit.getStatus() == CommitStatus.OK) {
          return config.getClient()
              .objects().blobState()
              .repo(config.getRepoName())
              .anyId(config.getHeadName())
              .blobNames(ids)
              .list().onItem()
              .transform(states -> {
                if(states.getStatus() != ObjectsStatus.OK) {
                  // TODO
                  throw new StoreException("LIST_FAIL", null, convertMessages2(states));
                }
                List<StoreEntity> entities = new ArrayList<>(); 
                for(final var state : states.getObjects().getBlob()) {
                  StoreEntity start = (StoreEntity) config.getDeserializer().fromString(state);
                  entities.add(start);
                }                  
                return entities;
              });
        }
        // TODO
        throw new StoreException("SAVE_FAIL", null, convertMessages(commit));
      });
    });
  }
  @Override
  public Uni<StoreEntity> delete(DeleteStoreEntity deleteType) {
    final Uni<EntityState> query = getEntityState(deleteType.getId());
    return query.onItem().transformToUni(state -> delete(state.getEntity()));
  }

  private String gid(BodyType type) {
    return config.getGidProvider().getNextId(type);
  }
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String repoName;
    private String headName;
    private ObjectMapper objectMapper;
    private PgSqlConfig.GidProvider gidProvider;
    private PgSqlConfig.AuthorProvider authorProvider;
    private io.vertx.mutiny.pgclient.PgPool pgPool;
    private String pgHost;
    private String pgDb;
    private Integer pgPort;
    private String pgUser;
    private String pgPass;
    private Integer pgPoolSize;
    
    public Builder repoName(String repoName) {
      this.repoName = repoName;
      return this;
    }
    public Builder objectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }
    public Builder gidProvider(PgSqlConfig.GidProvider gidProvider) {
      this.gidProvider = gidProvider;
      return this;
    }
    public Builder authorProvider(PgSqlConfig.AuthorProvider authorProvider) {
      this.authorProvider = authorProvider;
      return this;
    }
    public Builder pgPool(io.vertx.mutiny.pgclient.PgPool pgPool) {
      this.pgPool = pgPool;
      return this;
    }
    public Builder headName(String headName) {
      this.headName = headName;
      return this;
    }
    public Builder pgHost(String pgHost) {
      this.pgHost = pgHost;
      return this;
    }
    public Builder pgDb(String pgDb) {
      this.pgDb = pgDb;
      return this;
    }
    public Builder pgPort(Integer pgPort) {
      this.pgPort = pgPort;
      return this;
    }
    public Builder pgUser(String pgUser) {
      this.pgUser = pgUser;
      return this;
    }
    public Builder pgPass(String pgPass) {
      this.pgPass = pgPass;
      return this;
    }
    public Builder pgPoolSize(Integer pgPoolSize) {
      this.pgPoolSize = pgPoolSize;
      return this;
    }
    
    
    private PgSqlConfig.GidProvider getGidProvider() {
      return this.gidProvider == null ? type -> {
        return UUID.randomUUID().toString();
     } : this.gidProvider;
    }
    
    private PgSqlConfig.AuthorProvider getAuthorProvider() {
      return this.authorProvider == null ? ()-> "not-configured" : this.authorProvider;
    } 
    
    private ObjectMapper getObjectMapper() {
      if(this.objectMapper == null) {
        return this.objectMapper;
      }
      
      final ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new GuavaModule());
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.registerModule(new Jdk8Module());
      return objectMapper;
    }
    
    public PgSqlDialobStore build() {
      DialobAssert.notNull(repoName, () -> "repoName must be defined!");
    
      final var headName = this.headName == null ? "main": this.headName;
      if(LOGGER.isDebugEnabled()) {
        final var log = new StringBuilder()
          .append(System.lineSeparator())
          .append("Configuring Thena: ").append(System.lineSeparator())
          .append("  repoName: '").append(this.repoName).append("'").append(System.lineSeparator())
          .append("  headName: '").append(headName).append("'").append(System.lineSeparator())
          .append("  objectMapper: '").append(this.objectMapper == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          .append("  gidProvider: '").append(this.gidProvider == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          .append("  authorProvider: '").append(this.authorProvider == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          
          .append("  pgPool: '").append(this.pgPool == null ? "configuring" : "provided").append("'").append(System.lineSeparator())
          .append("  pgPoolSize: '").append(this.pgPoolSize).append("'").append(System.lineSeparator())
          .append("  pgHost: '").append(this.pgHost).append("'").append(System.lineSeparator())
          .append("  pgPort: '").append(this.pgPort).append("'").append(System.lineSeparator())
          .append("  pgDb: '").append(this.pgDb).append("'").append(System.lineSeparator())
          .append("  pgUser: '").append(this.pgUser == null ? "null" : "***").append("'").append(System.lineSeparator())
          .append("  pgPass: '").append(this.pgPass == null ? "null" : "***").append("'").append(System.lineSeparator());
          
        LOGGER.debug(log.toString());
      }
      
      final DocDB thena;
      if(pgPool == null) {
        DialobAssert.notNull(pgHost, () -> "pgHost must be defined!");
        DialobAssert.notNull(pgPort, () -> "pgPort must be defined!");
        DialobAssert.notNull(pgDb, () -> "pgDb must be defined!");
        DialobAssert.notNull(pgUser, () -> "pgUser must be defined!");
        DialobAssert.notNull(pgPass, () -> "pgPass must be defined!");
        DialobAssert.notNull(pgPoolSize, () -> "pgPoolSize must be defined!");
        
        final PgConnectOptions connectOptions = new PgConnectOptions()
            .setHost(pgHost)
            .setPort(pgPort)
            .setDatabase(pgDb)
            .setUser(pgUser)
            .setPassword(pgPass);
        final PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(pgPoolSize);
        
        final io.vertx.mutiny.pgclient.PgPool pgPool = io.vertx.mutiny.pgclient.PgPool.pool(connectOptions, poolOptions);
        
        thena = DocDBFactory.create().client(pgPool).db(repoName).build();
      } else {
        thena = DocDBFactory.create().client(pgPool).db(repoName).build();
      }
      
      final ObjectMapper objectMapper = getObjectMapper();
      final PgSqlConfig config = ImmutablePgSqlConfig.builder()
          .client(thena).repoName(repoName).headName(headName)
          .gidProvider(getGidProvider())
          .serializer((entity) -> {
            try {
              return objectMapper.writeValueAsString(ImmutableStoreEntity.builder().from(entity).hash("").build());
            } catch (IOException e) {
              throw new RuntimeException(e.getMessage(), e);
            }
          })
          .deserializer(new BlobDeserializer(objectMapper))
          .authorProvider(getAuthorProvider())
          .build();
      return new PgSqlDialobStore(config);
    }
  }
}
