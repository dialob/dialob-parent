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
package io.dialob.security.spring.apikey;

import java.util.Collection;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import io.dialob.security.key.ApiKey;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

  /**
   * Creates a token with the supplied array of authorities.
   *
   * @param authorities the collection of GrantedAuthority-s for the principal
   *                    represented by this authentication object.
   */
  public ApiKeyAuthenticationToken(@NonNull Collection<? extends GrantedAuthority> authorities,
                                   @NonNull ApiKey apikey) {
    super(authorities);
    Objects.requireNonNull(apikey);
    setDetails(apikey);
  }

  protected ApiKey getAPIKey() {
    return (ApiKey) getDetails();
  }

  @Override
  public Object getCredentials() {
    return getAPIKey().getHash().orElseGet(() -> getAPIKey().getToken().orElse(null));
  }

  @Override
  public Object getPrincipal() {
    return getAPIKey().getClientId();
  }

  @Override
  public boolean isAuthenticated() {
    return getAPIKey().isValid();
  }
}
