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
package io.dialob.security.spring.oauth2;

import feign.Param;
import feign.RequestLine;
import io.dialob.security.spring.oauth2.model.Token;

public interface OAuth2TokenService {

  @RequestLine("GET /oauth/token?grant_type=client_credentials&scope={scope}")
  Token getToken(@Param("scope") String scope);

}
