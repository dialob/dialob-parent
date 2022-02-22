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
package io.dialob.db.assets.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.form.service.api.FormDatabase.FormMetadataRow;
import io.dialob.form.service.api.ImmutableFormMetadataRow;

import java.io.IOException;

public class AssetFormMetadataRowDeserializer extends TemplateDeserializer {
  private final ObjectMapper objectMapper;

  public AssetFormMetadataRowDeserializer(ObjectMapper objectMapper) {
    super();
    this.objectMapper = objectMapper;
  }

  public FormMetadataRow deserialize(ObjectNode input) {
    try {
      JsonNode node = objectMapper.readTree(input.get("content").asText());
      Form.Metadata metadata = ImmutableFormMetadata.builder()
          .from(objectMapper.treeToValue(node, Form.Metadata.class))
          .putAdditionalProperties(getRevisions(input))
          .putAdditionalProperties(getTags(input))
          .build();
      String id = input.get("name").textValue();
      return ImmutableFormMetadataRow.of(id, metadata);
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}
