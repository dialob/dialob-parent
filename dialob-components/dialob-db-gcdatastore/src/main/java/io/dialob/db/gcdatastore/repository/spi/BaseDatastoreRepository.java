/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dialob.db.gcdatastore.repository.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.datastore.*;
import com.google.cloud.datastore.Entity.Builder;
import io.dialob.db.gcdatastore.repository.DatastoreRepository;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import io.dialob.db.spi.spring.AbstractDocumentDatabase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public abstract class BaseDatastoreRepository<T, ID extends Serializable> extends AbstractDocumentDatabase<T>
    implements DatastoreRepository<T, ID> {
  private static final String REV_PROPERTY_NAME = "_rev";
  protected final Datastore datastore;
  private final ObjectMapper mapper;
  private final String kind;
  private final String namespace;
  private Set<String> indexedProperties = new HashSet<>(Arrays.asList("_id", REV_PROPERTY_NAME));

  protected BaseDatastoreRepository(Datastore datastore, ObjectMapper mapper, String namespace, String kind, Class<? extends T> documentClass) {
    super(documentClass);
    this.datastore = datastore;
    this.mapper = mapper;
    this.namespace = namespace;
    this.kind = kind;
  }

  @Override
  public T save(T document) {

    Transaction transaction = datastore.newTransaction();
    try {
      checkDocumentRevision(document, transaction);
      // update next revision in entity instead of document to prevent its incrementing
      // in case of failed saving
      Entity entity = convert(document, getNextDocumentRev(document));
      Entity putEntity = transaction.put(entity);
      transaction.commit();
      T savedDocument = convert(putEntity);
      // workaround: db client does not use returned value but instead uses argument, therefore
      // we have to copy data from saved entity to argument and return it to maintain this compatibility
      document = updateDocumentId(document, id(savedDocument));
      document = updateDocumentRev(document, rev(savedDocument));
      return document;
    }
    finally {
      if (transaction.isActive()) {
        transaction.rollback();
      }
    }
  }

  private String getNextDocumentRev(T document) {
    String rev = rev(document);
    String newRev = rev != null ? (Integer.toString(Integer.parseInt(rev) + 1)) : "1";
    return newRev;
  }

  private void checkDocumentRevision(T document, Transaction transaction) {
    String id = id(document);
    String rev = rev(document);
    if (!StringUtils.isBlank(id)) {
      T previousVersion = convert(transaction.get(getKey(Long.valueOf(id))));
      if (previousVersion != null) {
        if (!StringUtils.equals(rev(previousVersion), rev)) {
          // same exception as datastore is using when concurrent update is detected
          throw new ConcurrentModificationException(id + " revision " + rev + " is out of date by newer version " + rev(previousVersion));
        }
      }
    }
  }


  @Override
  public T findOne(@NonNull ID id) {
    Entity entity = datastore.get(getKey(Long.valueOf(id.toString())));
    return convert(entity);
  }

  @Override
  public boolean exists(@NonNull ID id) {
    Entity entity = datastore.get(getKey(Long.valueOf(id.toString())));
    return entity != null;
  }

  @Override
  public void delete(@NonNull ID id) {
    datastore.delete(getKey(Long.valueOf(id.toString())));
  }

  private Key getKey(@NonNull Long key) {
    return getKeyFactory().newKey(key);
  }

  protected Entity convert(@NonNull T document, String rev) {
    Key key;
    KeyFactory keyFactory = getKeyFactory();
    if (!StringUtils.isEmpty(id(document))) {
      key = getKey(Long.valueOf(id(document)));
    } else {
      key = datastore.allocateId(keyFactory.newKey());
    }

    Builder builder = Entity.newBuilder(key);
    return convert(document, builder, rev);
  }

  private KeyFactory getKeyFactory() {
    KeyFactory keyFactory = datastore.newKeyFactory();
    if (getNamespace() != null) {
      keyFactory = keyFactory.setNamespace(getNamespace());
    }
    keyFactory = keyFactory.setKind(getKind());
    return keyFactory;
  }

  protected T convert(BaseEntity<Key> entity, Class<T> type) {
    T result = null;
    if (entity != null) {
      try {
        ObjectNode root = mapper.createObjectNode();
        for (String name : entity.getNames()) {
          root.set(name, mapper.readTree(entity.getString(name)));
        }
        result = mapper.treeToValue(root, type);
        result = updateDocumentId(result, entity.getKey().getNameOrId().toString());
      } catch (IOException e) {
        throw new DocumentCorruptedException(e.getMessage());
      }
    }
    return result;
  }

  protected Entity convert(T document, Builder builder, String rev) {
    Entity result;
    try {
      // build tree from document object and add all first level nodes as
      // separate properties to entity.
      // This stores each attribute into separate entity's property in datastore
      // instead of one big string value,
      // allowing document attribute indexing and using in queries.
      JsonNode tree = mapper.valueToTree(document);
      Iterator<Entry<String, JsonNode>> fields = tree.fields();
      while (fields.hasNext()) {
        Entry<String, JsonNode> val = fields.next();
        String fieldStringValue = mapper.writeValueAsString(val.getValue());
        if (isIndexed(val.getKey(), fieldStringValue)) {
          builder.set(val.getKey(), fieldStringValue);
        } else {
          builder.set(val.getKey(), StringValue.newBuilder(fieldStringValue).setExcludeFromIndexes(true).build());
        }
      }
      builder.set(REV_PROPERTY_NAME, rev);
      result = builder.build();
    } catch (JsonProcessingException e) {
      throw new DocumentCorruptedException(e.getMessage());
    }
    return result;
  }

  /**
   * Datastore allows up to 1500 bytes store into indexed string property. Values
   * exceeding this limit should be stored with exclusion from indexes to prevent
   * exceptions. NOTE: projection query can use only indexed properties.
   */
  private boolean isIndexed(String key, String fieldStringValue) {
    return indexedProperties.contains(key);
  }

  protected String getKind() {
    return kind;
  }

  /**
   * Subclass implementations should implement this method with call to
   * {@link #convert(Entity)} and providing T's class as second parameter.
   *
   * @param entity
   * @return
   */
  protected abstract T convert(Entity entity);

  protected String getNamespace() {
    return namespace;
  }
}
