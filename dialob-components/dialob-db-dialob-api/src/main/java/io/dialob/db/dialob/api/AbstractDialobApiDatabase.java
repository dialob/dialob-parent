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
package io.dialob.db.dialob.api;

import io.dialob.db.spi.spring.AbstractDocumentDatabase;
import org.springframework.lang.NonNull;

public abstract class AbstractDialobApiDatabase<T> extends AbstractDocumentDatabase<T> {
  private final DialobApiTemplate dialobApiTemplate;

  private final String resource;

  public AbstractDialobApiDatabase(DialobApiTemplate dialobApiTemplate, String resource, Class<T> entityClass) {
    super(entityClass);
    this.dialobApiTemplate = dialobApiTemplate;
    this.resource = resource;
  }

  @NonNull
  public T findOne(String tenantId, @NonNull String id, String rev) {
    return dialobApiTemplate.findOne(resource, id, rev, getDocumentClass());
  }

  @NonNull
  public T save(String tenantId, @NonNull T document) {
    return dialobApiTemplate.save(resource, id(document), document);
  }

  @NonNull
  public T findOne(String tenantId, @NonNull String id) {
    return findOne(tenantId, id, null);
  }

  public boolean exists(String tenantId, @NonNull String id) {
    throw new UnsupportedOperationException("exists not supported");
  }

  public boolean delete(String tenantId, @NonNull String id) {
    throw new UnsupportedOperationException("delete not supported");
  }


}
