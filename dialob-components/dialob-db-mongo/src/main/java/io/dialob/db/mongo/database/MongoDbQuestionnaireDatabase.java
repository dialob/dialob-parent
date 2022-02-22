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
package io.dialob.db.mongo.database;

import io.dialob.api.questionnaire.ModifiableQuestionnaire;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.mongo.MongoQuestionnaireIdObfuscator;
import io.dialob.db.mongo.repository.QuestionnaireRepository;
import io.dialob.questionnaire.service.api.ImmutableMetadataRow;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.function.Consumer;

public class MongoDbQuestionnaireDatabase extends BaseMongoDbDatabase<Questionnaire, ModifiableQuestionnaire, QuestionnaireRepository> implements QuestionnaireDatabase {

  private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbQuestionnaireDatabase.class);

  private final MongoQuestionnaireIdObfuscator mongoQuestionnaireIdObfuscator;

  public MongoDbQuestionnaireDatabase(final QuestionnaireRepository repository, MongoQuestionnaireIdObfuscator mongoQuestionnaireIdObfuscator) {
    super(repository);
    this.mongoQuestionnaireIdObfuscator = mongoQuestionnaireIdObfuscator;
  }

  @Override
  protected String toPublicId(String id) {
    return mongoQuestionnaireIdObfuscator.toPublicId(id);
  }

  @Override
  protected String toMongoId(String id) {
    return mongoQuestionnaireIdObfuscator.toMongoId(id);
  }

  @Override
  protected ModifiableQuestionnaire toMongo(Questionnaire document) {
    String extId = document.getId();
    String id = null;
    if (StringUtils.isBlank(extId)) {
      extId = null;
    }
    if (extId != null) {
      id = toMongoId(extId);
      if (id == null) {
        LOGGER.warn("DATA CORRUPT: Cannot convert external Id '{}' to mongo.", extId);
        return null;
      }
    }
    return ModifiableQuestionnaire.create().from(document).setId(id);
  }

  @Override
  protected Questionnaire toPublic(Questionnaire document) {
    return document;
  }

  @NonNull
  @Override
  public Questionnaire findOne(String tenantId, @NonNull String id) {
    return super.findOne("none", id);
  }

  @Override
  public boolean exists(String tenantId, @NonNull String id) {
    return super.exists("none", id);
  }

  @Override
  public boolean delete(String tenantId, @NonNull String id) {
    return super.delete("none", id);
  }

  @Override
  public void findAllMetadata(String tenantId, String ownerId, String formId, String formName, String formTag, Questionnaire.Metadata.Status status, @NonNull Consumer<MetadataRow> consumer) {
    doMongo(repository -> {
      repository.findAllMetadata().stream().forEach(document -> consumer.accept(ImmutableMetadataRow.of(toPublicId(document.getId()), document.getMetadata())));
      return null;
    });
  }

}
