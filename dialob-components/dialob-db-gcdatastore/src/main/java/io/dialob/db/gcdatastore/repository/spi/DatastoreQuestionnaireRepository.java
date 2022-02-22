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
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.gcdatastore.repository.QuestionnaireRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.LinkedList;
import java.util.List;

public class DatastoreQuestionnaireRepository extends BaseDatastoreRepository<Questionnaire, String>
    implements QuestionnaireRepository {
  String[] metadataProjection = { "metadata" };

  public DatastoreQuestionnaireRepository(Datastore datastore, ObjectMapper mapper, String namespace, String kind) {
    super(datastore, mapper, namespace, kind, Questionnaire.class);
  }

  @Override
  public List<Questionnaire> findAllMetadata() {
    // TODO: investigate whether projected entity can be used instead for better
    // performance:
    // if metatada size does not exceed datastore's indexed string property limit
    // 1500 bytes
    // then it's better to use projected entity query builder and add metadata
    // projection into query.
    List<Questionnaire> result = new LinkedList<>();
    Builder entityQueryBuilder = Query.newEntityQueryBuilder();
    String namespace = getNamespace();
    if (!StringUtils.isEmpty(namespace)) {
      entityQueryBuilder= entityQueryBuilder.setNamespace(namespace);
    }
    Query<Entity> query = entityQueryBuilder.setKind(getKind()).build();
    QueryResults<Entity> results = datastore.run(query);
    results.forEachRemaining(entity -> result.add(convert(entity, Questionnaire.class)));
    return result;
  }

  @Override
  protected Questionnaire convert(Entity entity) {
    return convert(entity, Questionnaire.class);
  }

  @NonNull
  @Override
  protected Questionnaire updateDocumentId(@NonNull Questionnaire document, String id) {
    return ImmutableQuestionnaire.builder().from(document).id(id).build();
  }

  @NonNull
  @Override
  protected Questionnaire updateDocumentRev(@NonNull Questionnaire document, String rev) {
    return ImmutableQuestionnaire.builder().from(document).rev(rev).build();
  }

}
