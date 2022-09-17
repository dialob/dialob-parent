package io.dialob.client.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.dialob.api.form.FormEntity;
import io.smallrye.mutiny.Uni;


public interface DialobStore {
  HistoryQuery history();
  Uni<StoreEntity> create(CreateStoreEntity newType);
  Uni<StoreEntity> update(UpdateStoreEntity updateType);
  Uni<StoreEntity> delete(DeleteStoreEntity deleteType);
  Uni<List<StoreEntity>> batch(List<StoreCommand> batchType);
    
  QueryBuilder query();
  String getRepoName();
  String getHeadName();
  StoreRepoBuilder repo();
  
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableStoreRelease.class)
  @JsonDeserialize(as = ImmutableStoreRelease.class)
  interface StoreRelease extends Serializable {
    String getName();
    LocalDateTime getCreated();
    List<StoreReleaseValue> getValues();
  }
  
  @Value.Immutable
  @JsonSerialize(as = ImmutableStoreReleaseValue.class)
  @JsonDeserialize(as = ImmutableStoreReleaseValue.class)
  interface StoreReleaseValue {
    String getHash();
    BodyType getBodyType();
    String getCommands();
  } 
  
  interface HistoryQuery {
    Uni<HistoryEntity> get(@Nonnull String id, @Nonnull String rev);
  }
  
  interface StoreRepoBuilder {
    StoreRepoBuilder repoName(String repoName);
    StoreRepoBuilder headName(String headName);
    Uni<DialobStore> create();    
    DialobStore build();
    Uni<Boolean> createIfNot();
  }
  
  interface QueryBuilder {
    Uni<StoreState> get();
    Uni<StoreEntity> get(String id);
  }

  @Value.Immutable @JsonSerialize(as = ImmutableStoreState.class) @JsonDeserialize(as = ImmutableStoreState.class)
  interface StoreState {
    Map<String, StoreEntity> getTags();
    Map<String, StoreEntity> getForms();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableStoreEntity.class) @JsonDeserialize(as = ImmutableStoreEntity.class)
  interface StoreEntity extends Serializable {
    String getId();
    String getVersion();
    BodyType getBodyType();
    String getHash();
    String getBody();
  }
  
  @Value.Immutable @JsonDeserialize(as = ImmutableHistoryEntity.class) @JsonSerialize(as = ImmutableHistoryEntity.class)
  interface HistoryEntity {
    String getId();
    FormEntity getBodyType();
    List<DetachedEntity> getBody();
  }
  
  @Value.Immutable @JsonDeserialize(as = ImmutableDetachedEntity.class) @JsonSerialize(as = ImmutableDetachedEntity.class)
  interface DetachedEntity {
    String getHash();
    LocalDateTime getCreated();
    FormEntity getBody();
  }
  
  interface StoreCommand extends Serializable {
    BodyType getBodyType();
  }

  @Value.Immutable @JsonSerialize(as = ImmutableUpdateStoreEntity.class) @JsonDeserialize(as = ImmutableUpdateStoreEntity.class)
  interface UpdateStoreEntity extends StoreCommand {
    String getId();
    String getVersion();
    String getBody();
  }

  @Value.Immutable @JsonSerialize(as = ImmutableCreateStoreEntity.class) @JsonDeserialize(as = ImmutableCreateStoreEntity.class)
  interface CreateStoreEntity extends StoreCommand {
    String getBody();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableDeleteStoreEntity.class) @JsonDeserialize(as = ImmutableDeleteStoreEntity.class)
  interface DeleteStoreEntity extends StoreCommand {
    String getId();
    String getVersion();
  }  

  
  enum BodyType { FORM, FORM_TAG }
  enum BodyStatus { OK, ERROR }
  
  @Value.Immutable @JsonSerialize(as = ImmutableBodySource.class) @JsonDeserialize(as = ImmutableBodySource.class)
  interface BodySource extends Serializable {
    String getId();
    String getHash();
    BodyType getBodyType();
    String getValue();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableStoreExceptionMsg.class)
  interface StoreExceptionMsg {
    String getId();
    String getValue();
    List<String> getArgs();
  }
  
}
