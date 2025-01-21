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
import io.dialob.api.form.Form;
import org.immutables.value.Value;

import java.util.function.Consumer;

/**
 * Represents an interface for managing form documents in a database.
 * Allows for operations such as retrieving, saving, checking existence,
 * deleting, and querying metadata of form documents.
 */
public interface FormDatabase {

  @NonNull
  Form findOne(@NonNull String tenantId, @NonNull String id, String rev);

  @NonNull
  Form findOne(@NonNull String tenantId, @NonNull String id);

  boolean exists(@NonNull String tenantId, @NonNull String id);

  // flush whole cache, ensure that revisioned forms are flushed too
  boolean delete(String tenantId, @NonNull String id);

  @NonNull
  Form save(String tenantId, @NonNull Form document);

  @Value.Immutable
  interface FormMetadataRow {
    @Value.Parameter
    @NonNull
    String getId();

    @Value.Parameter
    @NonNull
    Form.Metadata getValue();
  }

  void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer);
}
