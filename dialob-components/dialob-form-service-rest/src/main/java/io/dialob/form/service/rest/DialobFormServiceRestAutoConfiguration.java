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
package io.dialob.form.service.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.form.service.api.validation.FormItemCopier;
import io.dialob.integration.api.NodeId;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.session.engine.program.FormValidatorExecutor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
public class DialobFormServiceRestAutoConfiguration {

  private final FormsRestServiceController formsRestServiceController;
  private final FormTagsRestService formTagsRestService;

  public DialobFormServiceRestAutoConfiguration(
    ApplicationEventPublisher eventPublisher,
    FormDatabase formDatabase,
    Optional<FormVersionControlDatabase> formVersionControlDatabase,
    FormValidatorExecutor validator,
    FormIdRenamer renamer,
    ObjectMapper objectMapper,
    NodeId nodeId,
    FormItemCopier formItemCopier,
    CurrentTenant currentTenant,
    CurrentUserProvider currentUserProvider,
    Optional<Clock> clock)
  {
    this.formsRestServiceController = new FormsRestServiceController(eventPublisher, formDatabase, formVersionControlDatabase, validator, renamer, objectMapper, nodeId, formItemCopier, currentTenant, currentUserProvider, clock.orElse(Clock.systemDefaultZone()));
    this.formTagsRestService = new FormTagsRestServiceController(formVersionControlDatabase, currentTenant);
  }

  @Bean
  public FormsRestService formsRestService() {
    return formsRestServiceController;
  }

  @Bean
  public FormTagsRestService formTagsRestService() {
    return formTagsRestService;
  }

  @Bean
  public FormRootItemMustBeDefinedValidator formRootItemMustBeDefinedValidator() {
    return new FormRootItemMustBeDefinedValidator();
  }

  @Bean
  public FormApiExceptionHandlers formApiExceptionHandlers() {
    return new FormApiExceptionHandlers();
  }

}
