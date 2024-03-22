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

import io.dialob.security.user.CurrentUser;
import io.dialob.security.user.CurrentUserProvider;
import io.dialob.security.user.UnauthenticatedCurrentUserProvider;
import org.apache.commons.lang3.StringUtils;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OAuth2SpringSecurityCurrentUserProvider implements CurrentUserProvider {

  @NonNull
  @Override
  public CurrentUser get() {
    String userId = UnauthenticatedCurrentUserProvider.UNAUTHENTICATED;
    String displayName = userId;
    String firstName = null;
    String lastName = null;
    String email = null;
    final SecurityContext securityContext = SecurityContextHolder.getContext();
    if (securityContext != null) {
      final Authentication authentication = securityContext.getAuthentication();
      if (authentication != null) {

      	String authenticatedUserId = authentication.getName();
      	// preserve default "unauthenticated" if authentication returns empty string
      	if (!StringUtils.isEmpty(authenticatedUserId)) {
      		userId = authenticatedUserId;
      	}

        if (authentication instanceof OAuth2AuthenticationToken) {
          final OAuth2AuthenticationToken oAuth2Authentication = (OAuth2AuthenticationToken) authentication;
          final OAuth2User userAuthentication = oAuth2Authentication.getPrincipal();
          final Map<String,Object> attributes = userAuthentication.getAttributes();
          if (attributes != null) {
            email = (String) attributes.get("email");
            firstName = (String) attributes.get("given_name");
            lastName = (String) attributes.get("family_name");
            displayName = StringUtils.defaultIfBlank(firstName, "") + " " + StringUtils.defaultIfBlank(lastName, "");
          }
          return new CurrentUser(userId, displayName, firstName, lastName, email);
        }
      }
    }
    return UnauthenticatedCurrentUserProvider.UNAUTHENTICATED_USER;
  }
}
