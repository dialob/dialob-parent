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
package io.dialob.questionnaire.service.rest;

import io.dialob.questionnaire.csvserializer.CSVSerializer;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(QuestionnairesRestServiceController.class)
public class DialobQuestionnaireServiceRestAutoConfiguration {


  @Bean
  CSVSerializer csvSerializer(QuestionnaireDatabase questionnaireDatabase, CurrentTenant currentTenant) {
    return new CSVSerializer(questionnaireDatabase, currentTenant);
  }

  @Bean
  public DialobExceptionMapper dialobExceptionMapper() {
    return new DialobExceptionMapper();
  }

}
