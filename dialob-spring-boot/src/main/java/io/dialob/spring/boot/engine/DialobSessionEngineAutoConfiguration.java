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
package io.dialob.spring.boot.engine;

import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.validation.FormValidator;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionBuilder;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionBuilderFactory;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.rule.parser.api.RuleExpressionCompiler;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.session.engine.DialobProgramFromFormCompiler;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.DialobSessionUpdateHook;
import io.dialob.session.engine.QuestionnaireDialobProgramService;
import io.dialob.session.engine.program.*;
import io.dialob.session.engine.sp.AsyncFunctionInvoker;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionBuilder;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionSaveService;
import io.dialob.session.engine.sp.DialobQuestionnaireSessionService;
import io.dialob.spring.boot.redis.RedisQuestionnaireDialobSessionCacheConfiguration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@Import(RedisQuestionnaireDialobSessionCacheConfiguration.class)
public class DialobSessionEngineAutoConfiguration {

  @Bean
  public DialobProgramService dialobProgramService(
    FormDatabase formDatabase,
    CurrentTenant currentTenant,
    DialobProgramFromFormCompiler programFromFormCompiler) {
    return QuestionnaireDialobProgramService.newBuilder()
      .setFormDatabase((id, rev) -> formDatabase.findOne(currentTenant.getId(), id, rev))
      .setProgramFromFormCompiler(programFromFormCompiler).build();
  }

  @Bean
  public QuestionnaireSessionService questionnaireSessionService(
    QuestionnaireDatabase questionnaireDatabase,
    QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory,
    CurrentTenant currentTenant) {
    return new DialobQuestionnaireSessionService(
      questionnaireDatabase,
      questionnaireSessionBuilderFactory,
      currentTenant);
  }

  @Bean
  public QuestionnaireSessionSaveService questionnaireSessionSaveService(
    QuestionnaireDatabase questionnaireDatabase,
    CurrentTenant currentTenant)
  {
    return new DialobQuestionnaireSessionSaveService(
      questionnaireDatabase,
      currentTenant);
  }


  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public QuestionnaireSessionBuilder questionnaireSessionBuilder(QuestionnaireEventPublisher eventPublisher,
                                                                 FormDatabase formDatabase,
                                                                 DialobProgramService dialobProgramService,
                                                                 CurrentTenant currentTenant,
                                                                 DialobSessionEvalContextFactory sessionContextFactory,
                                                                 AsyncFunctionInvoker asyncFunctionInvoker,
                                                                 QuestionnaireSessionSaveService questionnaireSessionService) {
    return new DialobQuestionnaireSessionBuilder(eventPublisher,
      dialobProgramService,
      (id, rev) -> formDatabase.findOne(currentTenant.getId(), id, rev),
      questionnaireSessionService,
            sessionContextFactory,
      asyncFunctionInvoker);
  }

  @Bean
  public QuestionnaireSessionBuilderFactory questionnaireSessionBuilderFactory(ApplicationContext applicationContext) {
    return () -> applicationContext.getBean(QuestionnaireSessionBuilder.class);
  }

  @Bean
  public DialobSessionEvalContextFactory dialobSessionEvalContextFactory(FunctionRegistry functionRegistry,
                                                                         Optional<DialobSessionUpdateHook> dialobSessionEvalHooks) {
    return new DialobSessionEvalContextFactory(functionRegistry, dialobSessionEvalHooks.orElse(null));
  }

  @Bean
  public RuleExpressionCompiler ruleExpressionCompiler() {
    return new DialobRuleExpressionCompiler();
  }

  @Bean
  public DialobProgramFromFormCompiler programFromFormCompiler(FunctionRegistry functionRegistry) {
    return new DialobProgramFromFormCompiler(functionRegistry);
  }

  @Bean
  public FormValidator formValidator(DialobProgramFromFormCompiler programFromFormCompiler) {
    return new DialobFormValidator(programFromFormCompiler);
  }

  @Bean
  public FormValidator valueSetValidator() {
    return new ValueSetValidator();
  }

  @Bean
  public AsyncFunctionInvoker asyncFunctionInvoker(FunctionRegistry functionRegistry, QuestionnaireSessionService service) {
    return new AsyncFunctionInvoker(functionRegistry, service);
  }

  @Bean
  public FormValidatorExecutor formValidatorExecutor(List<FormValidator> formValidators) {
    return new FormValidatorExecutor(formValidators);
  }
}
