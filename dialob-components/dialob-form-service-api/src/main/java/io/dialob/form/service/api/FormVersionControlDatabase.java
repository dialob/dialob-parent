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
package io.dialob.form.service.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.form.FormTag;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining operations for form version control and management in a database.
 * Provides methods for handling forms, tags, snapshots, and version updates within
 * a tenant-specific context.
 */
public interface FormVersionControlDatabase {

  /**
   * Get form database service without version control support
   *
   * @return a non-null {@code FormDatabase} instance that provides methods for
   *         retrieving, saving, checking existence, deleting, and querying form documents.
   */
  @NonNull
  FormDatabase getFormDatabase();

  /**
   * Creates a new tag on the latest version of a form associated with a specific tenant.
   *
   * @param tenantId the identifier of the tenant to which the form belongs
   * @param formName the name of the form for which the tag will be created; must not be null
   * @param tag the name of the tag to be created
   * @param description a brief description of the tag; can be null
   * @param snapshot a flag indicating whether the tag creation involves creating a snapshot of the form
   * @return an {@code Optional<FormTag>} containing the created tag if successful, or an empty {@code Optional} if the operation fails
   */
  Optional<FormTag> createTagOnLatest(String tenantId, @NonNull String formName, String tag, String description, boolean snapshot);

  /**
   * Deletes the specified form associated with a tenant.
   *
   * @param tenantId the identifier of the tenant to which the form belongs
   * @param formName the name of the form to be deleted; must not be null
   * @return {@code true} if the form was successfully deleted, {@code false} otherwise
   */
  boolean delete(String tenantId, @NonNull String formName);

  /**
   * Deletes the specified tag associated with a form for a given tenant.
   *
   * @param tenantId the identifier of the tenant to which the form belongs
   * @param formName the name of the form for which the tag is to be deleted; must not be null
   * @param tag the name of the tag to be deleted
   * @return {@code true} if the tag was successfully deleted, {@code false} otherwise
   */
  boolean deleteTag(String tenantId, @NonNull String formName, String tag);

  /**
   * Creates a new tag associated with a form under the specified tenant.
   *
   * @param tenantId the unique identifier of the tenant to which the form belongs
   * @param formName the name of the form for which the tag will be created; must not be null
   * @param tag the name of the tag to be created
   * @param description a brief description of the tag; can be null
   * @param formDocumentIdOrRefName the document ID or reference name of the form to associate with the tag
   * @param type the type of the tag (e.g., NORMAL or MUTABLE); must not be null
   * @return an {@code Optional<FormTag>} containing the created tag if successful, or an empty {@code Optional} if the operation fails
   */
  Optional<FormTag> createTag(String tenantId, @NonNull String formName, String tag, String description, String formDocumentIdOrRefName, @NonNull FormTag.Type type);

  /**
   * Updates the label of a form associated with a specific tenant and form name.
   *
   * @param tenantId the identifier of the tenant to which the form belongs
   * @param formName the name of the form whose label is to be updated
   * @param label the new label to be assigned to the specified form
   * @return {@code true} if the label was successfully updated, {@code false} otherwise
   */
  boolean updateLabel(String tenantId, @NonNull String formName, String label);

  /**
   * Creates copy of form document and returns id of new form.

   * @param tenantId
   * @param formId
   * @return formId of new copy
   */
  String createSnapshot(String tenantId, @NonNull String formId);

  /**
   * @return true when formId is not form document id, but name.
   */
  boolean isName(String tenantId, @NonNull String formId);

  /**
   * Retrieves a list of tags associated with a specific form within a given tenant.
   *
   * @param tenantId the identifier of the tenant to which the form belongs
   * @param formId the unique identifier of the form
   * @param type the type of tags to retrieve; if null, all tag types are included
   * @return a list of {@code FormTag} objects matching the specified parameters
   */
  @NonNull
  List<FormTag> findTags(String tenantId, @NonNull String formId, @Nullable FormTag.Type type);

  /**
   * Retrieves a specific tag associated with a form for the given tenant.
   * If the `name` parameter is `null`, the method may use default behavior to determine the tag.
   *
   * @param tenantId the identifier of the tenant to which the form belongs
   * @param formName the name of the form
   * @param name the specific name of the tag to retrieve; if `null`, the behavior may vary depending on implementation
   * @return an {@code Optional<FormTag>} containing the tag if found, or an empty {@code Optional} if no matching tag exists
   */
  Optional<FormTag> findTag(String tenantId, @NonNull String formName, @Nullable String name);

  /**
   * Updates the latest form version associated with the specified tenant and form ID,
   * based on the provided {@code FormTag}.
   *
   * @param tenantId the identifier of the tenant to which the form belongs
   * @param formId the unique identifier of the form to be updated
   * @param tag the {@code FormTag} containing the information to update the form
   * @return {@code true} if the update operation succeeded, {@code false} otherwise
   */
  boolean updateLatest(String tenantId, @NonNull String formId, @NonNull FormTag tag);

  /**
   * Retrieves a list of tags associated with forms based on the provided parameters.
   *
   * @param tenantId the identifier of the tenant to which the forms belong
   * @param formName the name of the form for which the tags are queried; may be null
   * @param formId the unique identifier of the form for which the tags are queried; may be null
   * @param name the specific name of the tag to filter by; may be null
   * @param type the type of tags to retrieve; may be null to include all tag types
   * @return a list of {@code FormTag} objects matching the specified criteria, never null
   */
  @NonNull
  List<FormTag> queryTags(String tenantId, String formName, String formId, String name, FormTag.Type type);

  /**
   * Moves an existing tag associated with a form to a new location or updates
   * the tag details for the specified tenant.
   *
   * @param tag the {@code FormTag} object representing the updated tag information,
   *            including new details or the desired location for moving the tag
   * @return the updated {@code FormTag} wrapped in an {@code Optional} if the operation
   *         was successful, or an empty {@code Optional} if the operation failed
   */
  Optional<FormTag> moveTag(String tenantId, FormTag updateTag);
}
