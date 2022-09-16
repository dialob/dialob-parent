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
    QueryBuilder id(@Nonnull String id);
    QueryBuilder rev(String rev);
    Uni<StoreEntity> findOne();
    Uni<StoreState> get();
  }


  
  @Value.Immutable @JsonSerialize(as = ImmutableStoreState.class) @JsonDeserialize(as = ImmutableStoreState.class)
  interface StoreState {
    Map<String, StoreEntity> getTags();
    Map<String, StoreEntity> getForms();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableStoreEntity.class) @JsonDeserialize(as = ImmutableStoreEntity.class)
  interface StoreEntity extends Serializable {
    String getId();
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
    String getId();
    BodyType getType();
  }

  @Value.Immutable @JsonSerialize(as = ImmutableUpdateStoreEntity.class) @JsonDeserialize(as = ImmutableUpdateStoreEntity.class)
  interface UpdateStoreEntity extends StoreCommand {
    FormEntity getBody();
  }

  @Value.Immutable @JsonSerialize(as = ImmutableCreateStoreEntity.class) @JsonDeserialize(as = ImmutableCreateStoreEntity.class)
  interface CreateStoreEntity extends StoreCommand {
    FormEntity getBody();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableDeleteStoreEntity.class) @JsonDeserialize(as = ImmutableDeleteStoreEntity.class)
  interface DeleteStoreEntity extends StoreCommand { }  

  
  enum BodyType { FORM, TAG }
  enum BodyStatus { OK, ERROR }
  
  @Value.Immutable @JsonSerialize(as = ImmutableBodySource.class) @JsonDeserialize(as = ImmutableBodySource.class)
  interface BodySource extends Serializable {
    String getId();
    String getHash();
    BodyType getBodyType();
    String getValue();
  }
  
}
