package io.dialob.client.spi;

/*-
 * #%L
 * hdes-client-api
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dialob.client.api.DialobComposer.StoreDump;
import io.dialob.client.api.DialobComposer.StoreDumpValue;
import io.dialob.client.api.DialobStore;
import io.dialob.client.api.ImmutableStoreEntity;
import io.dialob.client.api.ImmutableStoreState;
import io.dialob.client.spi.store.StoreEntityLocation;
import io.dialob.client.spi.support.DialobAssert;
import io.dialob.client.spi.support.Sha2;
import io.smallrye.mutiny.Uni;

public class DialobMemoryStore implements DialobStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(DialobMemoryStore.class);
  private final Map<String, StoreEntity> entities;
  private final StoreState state;
  
  
  public DialobMemoryStore(Map<String, StoreEntity> entities) {
    super();
    this.entities = entities;
    final var builder = ImmutableStoreState.builder();
    for(final var entity : entities.values()) {
      switch (entity.getBodyType()) {
      case FORM_REV: builder.putRevs(entity.getId(), entity); break;
      case FORM: builder.putForms(entity.getId(), entity); break;
      default: continue;
      }
    }
    this.state = builder.build();
  }

  @Override
  public Uni<StoreEntity> create(CreateStoreEntity newType) {
    throw new RuntimeException("read only store!");
  }
  @Override
  public Uni<StoreEntity> update(UpdateStoreEntity updateType) {
    throw new RuntimeException("read only store!");
  }
  @Override
  public Uni<StoreEntity> delete(DeleteStoreEntity deleteType) {
    throw new RuntimeException("read only store!");
  }
  @Override
  public QueryBuilder query() {
    return new QueryBuilder() {
      @Override
      public Uni<StoreEntity> get(String id) {
        return Uni.createFrom().item(() -> entities.get(id));
      }
      @Override
      public Uni<StoreState> get() {
        return Uni.createFrom().item(() -> state);
      }
    };
  }

  
  public static Builder builder() {
    return new Builder();
  }
  
  public static class Builder {
    private ObjectMapper objectMapper;
    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final StoreEntityLocation location = new StoreEntityLocation("classpath*:assets/");
  
    private List<Resource> list(String location) {
      try {

        LOGGER.debug("Loading assets from: " + location + "!");
        List<Resource> files = new ArrayList<>();
        for (Resource resource : resolver.getResources(location)) {
          files.add(resource);
        }
        return files;
      } catch (Exception e) {
        throw new RuntimeException("Failed to load asset from: " + location + "!" + e.getMessage(), e);
      }
    }

    private StoreDump readDump(String json) {
      try {
        return objectMapper.readValue(json, StoreDump.class);
      } catch (Exception e) {
        throw new RuntimeException("Failed to load asset from: " + location + "!" + e.getMessage(), e);
      }
    }
    
    private ImmutableStoreEntity.Builder readStoreEntity(Resource resource) {
      final var content = readContents(resource);
      return ImmutableStoreEntity.builder()
          .id(resource.getFilename())
          .version("static_location")
          .hash(Sha2.blob(content))
          .body(content);
    }
    
    private String readContents(Resource entry) {
      try {
        return IOUtils.toString(entry.getInputStream(), StandardCharsets.UTF_8);
      } catch (IOException e) {
        throw new RuntimeException("Failed to load asset content from: " + entry.getFilename() + "!" + e.getMessage(), e);
      }
    }
    public Builder objectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
      return this;
    }
    
    private StoreRelease readRelease(String json) {
      try {
        return objectMapper.readValue(json, StoreRelease.class);
      } catch (Exception e) {
        throw new RuntimeException("Failed to load asset from: " + location + "!" + e.getMessage(), e);
      }
    }
    
    public DialobMemoryStore build() {
      DialobAssert.notNull(objectMapper, () -> "objectMapper must be defined!");
      final var migLog = new StringBuilder();
      final var entities = new HashMap<String, StoreEntity>();
      
      
      list(location.getMigrationRegex()).stream().forEach(r -> {
        final Map<BodyType, Integer> order = Map.of(
            BodyType.FORM_REV, 1,
            BodyType.FORM, 2);
        migLog
          .append("Loading assets from release: " + r.getFilename()).append(System.lineSeparator());
        
        final var assets = new ArrayList<>(readRelease(readContents(r)).getValues());
        assets.sort((StoreReleaseValue o1, StoreReleaseValue o2) -> 
          Integer.compare(order.get(o1.getBodyType()), order.get(o2.getBodyType()))
        );
        for(final var asset : assets) {
          migLog.append("  - ")
            .append(asset.getHash()).append("/").append(asset.getBodyType()).append("/").append(asset.getHash())
            .append(System.lineSeparator());
        
          final var id = UUID.randomUUID().toString();
          final var entity = ImmutableStoreEntity.builder()
              .id(id)
              .hash(asset.getHash())
              .body(asset.getCommands())
              .bodyType(asset.getBodyType())
              .build();
          entities.put(id, entity);
        }
      });
      

      list(location.getDumpRegex()).stream().forEach(r -> {
        
        
        final Map<BodyType, Integer> order = Map.of(
            BodyType.FORM_REV, 1,
            BodyType.FORM, 2);
        migLog
          .append("Loading assets from dump: " + r.getFilename()).append(System.lineSeparator());
        
        final var assets = new ArrayList<>(readDump(readContents(r)).getValue());
        assets.sort((StoreDumpValue o1, StoreDumpValue o2) -> 
          Integer.compare(order.get(o1.getBodyType()), order.get(o2.getBodyType()))
        );
        for(final var asset : assets) {
          migLog.append("  - ")
            .append(asset.getId()).append("/").append(asset.getBodyType()).append("/").append(asset.getHash())
            .append(System.lineSeparator());
        
          final var entity = ImmutableStoreEntity.builder()
              .id(asset.getId())
              .hash(asset.getHash())
              .body(asset.getValue())
              .bodyType(asset.getBodyType())
              .build();
          entities.put(entity.getId(), entity);
        }
        
        
      });
      
      
      migLog.append(System.lineSeparator());

      // form tags
      list(location.getFormTagRegex()).stream().forEach(r -> {
        final var entity = readStoreEntity(r).bodyType(BodyType.FORM_REV).build();    
        migLog.append("  - ")
          .append(entity.getId()).append("/").append(entity.getBodyType()).append("/").append(entity.getHash())
          .append(System.lineSeparator());
        entities.put(entity.getId(), entity);
      });

      // forms
      list(location.getFormRegex()).stream().forEach(r -> {
        final var entity = readStoreEntity(r).bodyType(BodyType.FORM).build();    
        migLog.append("  - ")
          .append(entity.getId()).append("/").append(entity.getBodyType()).append("/").append(entity.getHash())
          .append(System.lineSeparator());
        entities.put(entity.getId(), entity);
      });
      LOGGER.debug(migLog.toString());
      return new DialobMemoryStore(entities);
    }
  }
  @Override
  public StoreRepoBuilder repo() {
    throw new IllegalArgumentException("not implemented");
  }
  @Override
  public Uni<List<StoreEntity>> batch(List<StoreCommand> batchType) {
    throw new IllegalArgumentException("not implemented");
  }
  @Override
  public String getRepoName() {
    return "in-memory";
  }
  @Override
  public String getHeadName() {
    return "in-memory";
  }
}
