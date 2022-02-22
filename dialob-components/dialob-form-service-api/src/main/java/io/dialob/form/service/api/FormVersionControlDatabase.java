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
package io.dialob.form.service.api;

import io.dialob.api.form.FormTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Form database with version control support
 */
public interface FormVersionControlDatabase {

  /**
   * Get form database service without version control support
   *
   * @return form database service
   */
  @Nonnull
  FormDatabase getFormDatabase();

  /**
   *
   * @param tenantId
   * @param formName
   * @param tag
   * @param description
   * @param snapshot create snapshot of form before tagging
   * @return
   */
  Optional<FormTag> createTagOnLatest(String tenantId, @Nonnull String formName, String tag, String description, boolean snapshot);

  boolean delete(String tenantId, @Nonnull String formName);

  boolean deleteTag(String tenantId, @Nonnull String formName, String tag);

  Optional<FormTag> createTag(String tenantId, @Nonnull String formName, String tag, String description, String formDocumentIdOrRefName, @Nonnull FormTag.Type type);

  boolean updateLabel(String tenantId, @Nonnull String formName, String label);

  /**
   * Creates copy of form document and returns id of new form.
   *
   *
   * @param tenantId
   * @param formId
   * @return formId of new copy
   */
  String createSnapshot(String tenantId, @Nonnull String formId);

  /**
   * @return true when formId is not form document id, but name.
   */
  boolean isName(String tenantId, @Nonnull String formId);

  @Nonnull
  List<FormTag> findTags(String tenantId, @Nonnull String formId, @Nullable FormTag.Type type);

  Optional<FormTag> findTag(String tenantId, @Nonnull String formName, @Nullable String name);

  boolean updateLatest(String tenantId, @Nonnull String formId, @Nonnull FormTag tag);

  @Nonnull
  List<FormTag> queryTags(String tenantId, String formName, String formId, String name, FormTag.Type type);

  /**
   *
   *
   * @param tenantId
   * @param updateTag
   * @return
   */
  Optional<FormTag> moveTag(String tenantId, FormTag updateTag);
}
