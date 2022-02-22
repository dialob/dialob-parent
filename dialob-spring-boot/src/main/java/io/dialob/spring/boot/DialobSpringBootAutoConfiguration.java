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
package io.dialob.spring.boot;

import io.dialob.rule.parser.function.DefaultFunctions;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.spring.boot.redis.RedisQuestionnaireDialobSessionCacheConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration(proxyBeanMethods = false)
@Import({
  RedisQuestionnaireDialobSessionCacheConfiguration.class
})
@ImportResource("classpath:dialob-spring-boot-cache-context.xml")
public class DialobSpringBootAutoConfiguration {
  @Bean
  @ConditionalOnBean(FunctionRegistry.class)
  public DefaultFunctions defaultFunctions(FunctionRegistry functionRegistry) {
    return new DefaultFunctions(functionRegistry);
  }


}
