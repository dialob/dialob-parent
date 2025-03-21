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
package io.dialob.boot.controller;

import io.dialob.security.spring.tenant.ImmutableTenantGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class WithDialobUserSecurityContextFactory implements
  WithSecurityContextFactory<WithDialobUser> {
  @Override
  public SecurityContext createSecurityContext(WithDialobUser withUser) {
    String username = StringUtils.hasLength(withUser.username()) ? withUser
      .username() : withUser.value();
    if (username == null) {
      throw new IllegalArgumentException(withUser
        + " cannot have null username on both username and value properites");
    }

    List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    for (String authority : withUser.authorities()) {
      grantedAuthorities.add(new SimpleGrantedAuthority(authority));
    }
    for (String tenant : withUser.tenants()) {
      grantedAuthorities.add(ImmutableTenantGrantedAuthority.of(tenant,tenant));
    }

    User principal = new User(username, withUser.password(), true, true, true, true,
      grantedAuthorities);
    Authentication authentication = new UsernamePasswordAuthenticationToken(
      principal, principal.getPassword(), principal.getAuthorities());
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    return context;
  }
}
