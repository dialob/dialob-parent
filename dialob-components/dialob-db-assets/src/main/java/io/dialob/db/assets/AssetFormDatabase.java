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
package io.dialob.db.assets;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.Form;
import io.dialob.db.assets.repository.AssetRepository;
import io.dialob.db.assets.serialization.AssetFormDeserializer;
import io.dialob.db.assets.serialization.AssetFormMetadataRowDeserializer;
import io.dialob.db.assets.serialization.AssetFormSerializer;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;

import java.util.function.Consumer;

public class AssetFormDatabase implements FormDatabase {

  private final AssetRepository assetRepository;
  private final AssetFormSerializer assetFormSerializer;
  private final AssetFormDeserializer assetFormDeserializer;
  private final AssetFormMetadataRowDeserializer assetFormMetadataRowDeserializer;

  public AssetFormDatabase(
      AssetRepository assetRepository, AssetFormSerializer assetFormSerializer,
      AssetFormDeserializer assetFormDeserializer,
      AssetFormMetadataRowDeserializer assetFormMetadataRowDeserializer) {
    super();
    this.assetRepository = assetRepository;
    this.assetFormSerializer = assetFormSerializer;
    this.assetFormDeserializer = assetFormDeserializer;
    this.assetFormMetadataRowDeserializer = assetFormMetadataRowDeserializer;
  }

  @NonNull
  @Override
  public Form findOne(@NonNull String tenantId, @NonNull String id, String rev) {
    return assetFormDeserializer.deserialize(
        assetRepository.createQuery().id(id).rev(rev).get()
        .orElseThrow(() -> new DocumentNotFoundException(String.format("Can't find document by id = %s, rev = %s!", id, rev))));
  }

  @NonNull
  @Override
  public Form findOne(@NonNull String tenantId, @NonNull String id) {
    return findOne(tenantId, id, null);
  }

  @Override
  public boolean exists(@NonNull String tenantId, @NonNull String id) {
    return assetRepository.createQuery().id(id).get().isPresent();
  }

  @Override
  public boolean delete(String tenantId, @NonNull String id) {
    assetRepository.createQuery().id(id).delete();
    return true;
  }

  @NonNull
  @Override
  public Form save(String tenantId, @NonNull Form document) {
    return assetFormDeserializer.deserialize(assetRepository.createBuilder().document(assetFormSerializer.serialize(document)).build());
  }

  @Override
  public void findAllMetadata(String tenantId, Form.Metadata metadata, @NonNull Consumer<FormMetadataRow> consumer) {
    assetRepository.createQuery().metadata().list().forEach(e -> consumer.accept(assetFormMetadataRowDeserializer.deserialize(e)));
  }
}
