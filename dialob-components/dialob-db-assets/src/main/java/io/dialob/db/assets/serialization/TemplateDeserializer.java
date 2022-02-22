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
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateDeserializer {
  private static final String REV = "rev";
  private static final String REVS = "revs";
  private static final String TAGS = "tags";

  protected boolean isRevision(ObjectNode input) {
    return input.hasNonNull(REV);
  }

  protected String getRevision(ObjectNode input) {
    return input.get(REV).asText();
  }

  protected Map.Entry<String, List<String>> getRevisions(JsonNode asset) {
    List<String> revisions = new ArrayList<>();
    if(asset.hasNonNull(REVS)) {
      JsonNode node = asset.get(REVS);
      node.forEach(n -> revisions.add(n.asText()));
    }
    return new AbstractMap.SimpleImmutableEntry<>("revisions", revisions);
  }

  protected Map.Entry<String, List<String>> getTags(JsonNode asset) {
    List<String> revisions = new ArrayList<>();
    if(asset.hasNonNull(TAGS)) {
      JsonNode node = asset.get(TAGS);
      node.forEach(n -> revisions.add(n.asText()));
    }
    return new AbstractMap.SimpleImmutableEntry<>(TAGS, revisions);
  }
}
