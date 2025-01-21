/*
 * Copyright © 2015 - 2025 ReSys (info@dialob.io)
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

/**
 * Interface defining operations for managing and querying questionnaires within a database.
 */
public interface QuestionnaireDatabase {

  /**
   * Retrieves a single questionnaire document from the database based on the provided identifier.
   *
   * @param tenantId the tenant identifier associated with the questionnaire; can be null.
   * @param id the unique identifier of the questionnaire; must not be null.
   * @param rev the revision identifier to retrieve a specific version of the questionnaire; optional.
   * @return the retrieved Questionnaire object; never null.
   */
  @NonNull
  Questionnaire findOne(String tenantId, @NonNull String id, String rev);

  /**
   * Retrieves a single questionnaire document from the database based on the provided tenant and questionnaire identifier.
   *
   * @param tenantId the identifier of the tenant associated with the questionnaire; can be null.
   * @param id the unique identifier of the questionnaire; must not be null.
   * @return the retrieved Questionnaire object; never null.
   */
  @NonNull
  Questionnaire findOne(String tenantId, @NonNull String id);

  /**
   * Checks if a questionnaire exists in the database based on the provided tenant and unique questionnaire identifier.
   *
   * @param tenantId the identifier of the tenant associated with the questionnaire; can be null.
   * @param id the unique identifier of the questionnaire; must not be null.
   * @return true if the questionnaire exists, false otherwise.
   */
  boolean exists(String tenantId, @NonNull String id);

  /**
   * Deletes a questionnaire from the database based on the provided tenant and unique questionnaire identifiers.
   *
   * @param tenantId the identifier of the tenant associated with the questionnaire; can be null.
   * @param id the unique identifier of the questionnaire; must not be null.
   * @return true if the questionnaire was successfully deleted, false if it could not be found.
   */
  boolean delete(String tenantId, @NonNull String id);

  /**
   * Saves a questionnaire document in the database associated with the specified tenant identifier.
   *
   * @param tenantId the identifier of the tenant associated with the questionnaire; can be null.
   * @param document the questionnaire document to be saved; must not be null.
   * @return the saved Questionnaire object; never null.
   */
  @NonNull
  Questionnaire save(String tenantId, @NonNull Questionnaire document);

  /**
   * Retrieves metadata for a specific questionnaire associated with the provided tenant identifier.
   *
   * This method constructs a {@code MetadataRow} object containing the questionnaire's
   * unique identifier and its metadata.
   *
   * @param tenantId the identifier of the tenant associated with the questionnaire; can be null.
   * @param questionnaireId the unique identifier of the questionnaire; must not be null.
   * @return a {@code MetadataRow} containing the questionnaire's identifier and metadata; never null.
   */
  default MetadataRow findMetadata(String tenantId, String questionnaireId) {
    return ImmutableMetadataRow.builder().id(questionnaireId).value(findOne(tenantId, questionnaireId).getMetadata()).build();
  }

  /**
   * Represents a row of metadata associated with a questionnaire.
   * This is an immutable interface that defines two primary attributes:
   * an identifier and a metadata value.
   *
   * It is part of the QuestionnaireDatabase and serves as a data transfer object
   * for carrying questionnaire metadata information through the application layers.
   *
   * Implementations of this interface ensure immutability and serialization capabilities.
   * The {@code id} uniquely identifies the metadata, while the {@code value}
   * holds the actual metadata details represented by {@link Questionnaire.Metadata}.
   */
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
   * Retrieves all metadata entries from the database that match the provided filters and
   * processes each entry using the given consumer.
   *
   * @param tenantId the identifier of the tenant; can be null to include all tenants.
   * @param ownerId the identifier of the owner of the metadata; can be null to include all owners.
   * @param formId the unique identifier of the form associated with the metadata; can be null to include all forms.
   * @param formName the name of the form; can be null to include all form names.
   * @param formTag the tag of the form; can be null to include all tags.
   * @param status the status of the metadata (e.g., NEW, OPEN, COMPLETED); can be null to include all statuses.
   * @param consumer the consumer to process each metadata entry; must not be null.
   */
  void findAllMetadata(@Nullable String tenantId,
                       @Nullable String ownerId,
                       @Nullable String formId,
                       @Nullable String formName,
                       @Nullable String formTag,
                       @Nullable Questionnaire.Metadata.Status status,
                       @NonNull Consumer<MetadataRow> consumer);

}
