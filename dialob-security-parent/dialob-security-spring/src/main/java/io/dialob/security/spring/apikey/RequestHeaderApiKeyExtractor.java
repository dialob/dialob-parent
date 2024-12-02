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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.security.UUIDUtils;
import io.dialob.security.key.ApiKey;
import io.dialob.security.key.ImmutableApiKey;
import io.dialob.security.key.ServletRequestApiKeyExtractor;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;


@Slf4j
public class RequestHeaderApiKeyExtractor implements ServletRequestApiKeyExtractor {

  public static final String REQUEST_APIKEY_ATTRIBUTE = ApiKey.class.getCanonicalName();

  private static final int UUID_LENGTH = 16;

  private final String header;

  public RequestHeaderApiKeyExtractor() {
    this("x-api-key");
  }

  public RequestHeaderApiKeyExtractor(String header) {
    this.header = Objects.requireNonNull(header);
  }

  @Override
  public ApiKey extract(ServletRequest request) {
    ApiKey apiKey = (ApiKey) request.getAttribute(REQUEST_APIKEY_ATTRIBUTE);
    if (apiKey != null) {
      return apiKey;
    }
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    final String requestHeader = httpServletRequest.getHeader(this.header);
    if (requestHeader != null) {
      apiKey = createApiKey(requestHeader);
      if (apiKey != null) {
        request.setAttribute(REQUEST_APIKEY_ATTRIBUTE, apiKey);
      }
    }
    return apiKey;
  }

  protected ApiKey createApiKey(@NonNull String header) {
    try {
      byte[] token = Base64.getDecoder().decode(header);
      if (token.length < UUID_LENGTH) {
        return null;
      }
      final String clientId = decodeClientId(Arrays.copyOf(token, UUID_LENGTH));
      String tokenHash = decodeToken(Arrays.copyOfRange(token, UUID_LENGTH, token.length));
      if (StringUtils.isBlank(tokenHash)) {
        return null;
      }
      return ImmutableApiKey.of(clientId).withToken(tokenHash);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Invalid api key request");
      return null;
    }
  }

  @Nullable
  protected String decodeToken(@NonNull byte[] token) {
    return Base64.getEncoder().encodeToString(token);
  }

  @NonNull
  protected String decodeClientId(@NonNull byte[] uuidBytes) {
    return UUIDUtils.toUUID(uuidBytes).toString();
  }

  @NonNull
  @Override
  public String getHeader() {
    return header;
  }
}
