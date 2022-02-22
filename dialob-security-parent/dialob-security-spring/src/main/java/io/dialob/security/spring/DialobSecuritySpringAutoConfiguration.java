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
package io.dialob.security.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.dialob.security.spring.audit.AuditConfiguration;
import io.dialob.security.spring.filter.MDCRequestIdFilter;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.security.user.UnauthenticatedCurrentUserProvider;

@Configuration(proxyBeanMethods = false)
@Import(AuditConfiguration.class)
public class DialobSecuritySpringAutoConfiguration {

  @Bean
  @ConditionalOnProperty(name = "dialob.security.enabled", havingValue = "false", matchIfMissing = true)
  public CurrentUserProvider anonymousCurrentUserProvider() {
    return UnauthenticatedCurrentUserProvider.INSTANCE;
  }

  @Bean
  public FilterRegistrationBean requestIdFilter() {
    FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
    filterRegBean.setFilter(new MDCRequestIdFilter());
    filterRegBean.setOrder(Integer.MIN_VALUE);
    return filterRegBean;
  }

}
