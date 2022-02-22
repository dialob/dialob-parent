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
package io.dialob.cloud.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.questionnaire.service.api.AnswerSubmitHandler;

import java.util.HashMap;
import java.util.Map;

class S3NormalizingPostSubmitHandlerTest {

//  @Test
  public void test() {
    S3NormalizingPostSubmitHandler handler = new S3NormalizingPostSubmitHandler(new ObjectMapper());
    AnswerSubmitHandler.Settings settings = new AnswerSubmitHandler.Settings() {

      Map<String,Object> properties = new HashMap<>();

      @Override
      public String getBeanName() {
        return null;
      }

      @Override
      public Map<String, Object> getProperties() {
        return properties;
      }
    };
    Map<String, Object> entries = new HashMap<>();

    entries.put("_id", "123");
    settings.getProperties().put("bucket", "op-air-test");
    settings.getProperties().put("region", "eu-central-1");
    settings.getProperties().put("accessKey", "AKIAYA5AOOWYS2THI437");
    settings.getProperties().put("secretKey", "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");
//    settings.getProperties().put("bucket", "op-air-test");

//    region: eu-central-1
//    accessKey: ***
//    secretKey: ***
//    bucket: op-air-test


    handler.sendDocument(settings, entries);


  }

}
