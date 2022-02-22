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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.db.gcdatastore.repository.FormRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.LinkedList;
import java.util.List;

public class DatastoreFormRepository extends BaseDatastoreRepository<Form, String> implements FormRepository {

  public DatastoreFormRepository(Datastore datastore, ObjectMapper mapper, String namespace, String kind) {
    super(datastore, mapper, namespace, kind, Form.class);
  }

  @Override
  public List<Form> findAllMetadata() {
    // more effective would be to load projection entities with metadata only but
    // metadata exceeds 1500 bytes limit for forms with more than couple fields,
    // thus loading all data.
    // Generally there isn't many forms so this shouldn't cause performance issues.
    List<Form> result = new LinkedList<>();
    Builder entityQueryBuilder = Query.newEntityQueryBuilder();
    String namespace = getNamespace();
    if (!StringUtils.isEmpty(namespace)) {
      entityQueryBuilder= entityQueryBuilder.setNamespace(namespace);
    }
    Query<Entity> query = entityQueryBuilder.setKind(getKind()).build();
    QueryResults<Entity> results = datastore.run(query);
    results.forEachRemaining(entity -> result.add(convert(entity, Form.class)));
    return result;
  }

  @Override
  protected Form convert(Entity entity) {
    return convert(entity, Form.class);
  }

  @NonNull
  @Override
  protected Form updateDocumentId(@NonNull Form form, String id) {
    return ImmutableForm.builder().from(form).id(id).build();
  }

  @NonNull
  @Override
  protected Form updateDocumentRev(@NonNull Form from, String rev) {
    return ImmutableForm.builder().from(from).rev(rev).build();
  }

}
