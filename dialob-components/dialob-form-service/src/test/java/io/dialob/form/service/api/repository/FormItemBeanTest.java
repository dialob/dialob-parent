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
package io.dialob.form.service.api.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.form.FormItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FormItemBeanTest {
  public FormItem parseFormItem(String data) throws IOException {
    final ObjectMapper objectMapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    JavaTimeModule javaTimeModule = new JavaTimeModule();
    objectMapper.registerModule(javaTimeModule);
    objectMapper.registerModule(module);
    JsonParser parser = objectMapper.getFactory().createParser(data);
    return parser.readValueAs(FormItem.class);
  }

  @Test
  void test() throws Exception {
    FormItem formItem = parseFormItem("{\"id\":\"item1\",\"type\":\"text\"}");
    Assertions.assertEquals("item1", formItem.getId());
    Assertions.assertEquals("text", formItem.getType());
  }

}
