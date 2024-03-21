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
package io.dialob.session.rest;

import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.settings.DialobSettings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration(proxyBeanMethods = false)
public class DialobSessionRestAutoConfiguration {

  @Bean
  @ConditionalOnProperty(prefix = "dialob.session.rest", name = "enabled", havingValue = "true")
  public AnswerController answerController(
    QuestionnaireSessionService questionnaireSessionService,
    ActionProcessingService actionProcessingService,
    SessionPermissionEvaluator sessionPermissionEvaluator,
    DialobSettings dialobSettings,
    Optional<CurrentUserProvider> currentUserProvider)
  {
    return new DefaultAnswerController(
      questionnaireSessionService,
      actionProcessingService,
      sessionPermissionEvaluator,
      dialobSettings.getSession().isReturnStackTrace(),
      currentUserProvider);
  }


  @Bean
  @ConditionalOnMissingBean(SessionPermissionEvaluator.class)
  public SessionPermissionEvaluator sessionPermissionEvaluator() {
    return (sessionId, userId) -> true;
  }




}
