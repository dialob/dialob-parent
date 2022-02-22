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

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import io.dialob.security.spring.apikey.ApiKeyAuthenticationToken;
import io.dialob.security.user.CurrentUser;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.security.user.UnauthenticatedCurrentUserProvider;

public class ApiKeyCurrentUserProvider implements CurrentUserProvider {
  @NonNull
  @Override
  public CurrentUser get() {
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    if (securityContext != null) {
      final Authentication authentication = securityContext.getAuthentication();
      if (authentication instanceof ApiKeyAuthenticationToken) {
        final ApiKeyAuthenticationToken oAuth2Authentication = (ApiKeyAuthenticationToken) authentication;
        String userId = (String) oAuth2Authentication.getPrincipal();
        return new CurrentUser(userId, userId, null, null, null);
      }
    }
    return UnauthenticatedCurrentUserProvider.UNAUTHENTICATED_USER;
  }
}
