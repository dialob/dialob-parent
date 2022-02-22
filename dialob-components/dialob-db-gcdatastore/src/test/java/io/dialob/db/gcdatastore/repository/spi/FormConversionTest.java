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
package io.dialob.db.gcdatastore.repository.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class FormConversionTest {

  public final ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule());

  @Test
  public void test() throws JsonProcessingException {
    DatastoreFormRepository repo = new DatastoreFormRepository(null, objectMapper, null, "forms");
    final Form form = ImmutableForm.builder()
      .id("formid")
      .rev("1")
      .metadata(ImmutableFormMetadata.builder()
        .created(new Date())
        .creator("Tester")
        .defaultSubmitUrl("http://localhost:9080/")
        .label("Label")
        .tenantId("tenant_id")
        .build())
      .putData("questionnaire", ImmutableFormItem.builder().id("item").type("itemtype").putLabel("en", "dialob dialog").build())
      .build();

    Key key = null;
    KeyFactory keyFactory = new KeyFactory("test").setKind("test");
    key = keyFactory.newKey(form.getId());

    Builder builder = Entity.newBuilder(key);
    Entity convert = repo.convert(form, builder, form.getRev());

    Form convertedDocument = repo.convert(convert, Form.class);
    Assertions.assertNotNull(convertedDocument);
    Assertions.assertEquals(form, convertedDocument);
  }

}
