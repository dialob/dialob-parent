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
package io.dialob.form.service;

import io.dialob.form.service.api.validation.CsvToFormParser;
import io.dialob.form.service.api.validation.FormIdRenamer;
import io.dialob.form.service.api.validation.FormItemCopier;
import io.dialob.rule.parser.api.RuleExpressionCompiler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration(proxyBeanMethods = false)
@ImportResource("classpath:dialob-form-service-cache-context.xml")
public class DialobFormServiceAutoConfiguration {

  @Bean
  public FormIdRenamer formIdRenamer(RuleExpressionCompiler ruleExpressionCompiler) {
    return new DialobFormIdRenamer(ruleExpressionCompiler);
  }

  @Bean
  public FormItemCopier formItemCopier(RuleExpressionCompiler compiler, FormIdRenamer renamerService) {
    return new DialobFormItemCopier(compiler, renamerService);
  }

  @Bean
  public CsvToFormParser csvToFormParser() {
    return new DialobCsvToFormParser();
  }

}
