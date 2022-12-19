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
package io.dialob.questionnaire.service.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.questionnaire.Questionnaire;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.function.Consumer;

public interface QuestionnaireDatabase {

  @NonNull
  Questionnaire findOne(String tenantId, @NonNull String id, String rev);

  @NonNull
  Questionnaire findOne(String tenantId, @NonNull String id);

  boolean exists(String tenantId, @NonNull String id);

  boolean delete(String tenantId, @NonNull String id);

  @NonNull
  Questionnaire save(String tenantId, @NonNull Questionnaire document);

  default MetadataRow findMetadata(String tenantId, String questionnaireId) {
    return ImmutableMetadataRow.builder().id(questionnaireId).value(findOne(tenantId, questionnaireId).getMetadata()).build();
  }

  @Value.Immutable
  interface MetadataRow extends Serializable {
    @NonNull
    @Value.Parameter
    String getId();

    @NonNull
    @Value.Parameter
    Questionnaire.Metadata getValue();
  }

  /**
   *
   * @param tenantId search questionnaires within given tenant
   * @param ownerId search questionnaires by owner
   * @param formId search questionnaires by form id
   * @param formName search questionnaires by form name
   * @param formTag search questionnaires by form tag. This is ignored, if formName is null.
   * @param status search questionnaires by status
   * @param consumer
   */
  void findAllMetadata(@Nullable String tenantId,
                       @Nullable String ownerId,
                       @Nullable String formId,
                       @Nullable String formName,
                       @Nullable String formTag,
                       @Nullable Questionnaire.Metadata.Status status,
                       @NonNull Consumer<MetadataRow> consumer);

}
