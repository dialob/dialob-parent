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
package io.dialob.boot.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.dialob.common.Permissions;

public class ActuatorEndpointSecurityConfigurer extends WebSecurityConfigurerAdapter implements Ordered {

  private int order;

  private final RequestMatcher GET_REQUEST = request -> "GET".equals(request.getMethod());

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.securityMatcher(EndpointRequest.toAnyEndpoint())
      .authorizeHttpRequests()
      .requestMatchers(new AndRequestMatcher(
        EndpointRequest.to(HealthEndpoint.class),
        GET_REQUEST)).permitAll()
      .anyRequest().hasAuthority(Permissions.AUDIT);
  }

  @Override
  public int getOrder() {
    return order;
  }

  public ActuatorEndpointSecurityConfigurer withOrder(int order) {
    this.order = order;
    return this;
  }
}
