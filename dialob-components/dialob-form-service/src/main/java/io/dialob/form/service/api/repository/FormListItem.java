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
package io.dialob.form.service.api.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableFormMetadata;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormListItem implements Serializable {

  @JsonProperty
  private String id;

  @JsonProperty
  private Form.Metadata metadata;

  public FormListItem() {

  }

  public FormListItem(String id, Form.Metadata metadata) {
    this.id = id;
    this.metadata = metadata;
  }

  private void ensureMetadata() {
    if (this.metadata == null) {
      this.metadata = ImmutableFormMetadata.builder().label("New Form").build();
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Form.Metadata getMetadata() {
    ensureMetadata();
    return metadata;
  }

  public void setMetadata(Form.Metadata metadata) {
    this.metadata = metadata;
  }
}
