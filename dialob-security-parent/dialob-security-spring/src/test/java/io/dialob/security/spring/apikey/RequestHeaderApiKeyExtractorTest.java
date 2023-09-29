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

import io.dialob.security.key.ApiKey;
import io.dialob.security.key.ImmutableApiKey;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestHeaderApiKeyExtractorTest {

  @Test
  public void shouldDecodeValidKey() {
    //String t = Base64.getEncoder().encodeToString(UUIDUtils.toBytes(UUID.fromString("00000000-0000-0000-0000-000000000000")));
    RequestHeaderApiKeyExtractor extractor = new RequestHeaderApiKeyExtractor();
    ApiKey apiKey = extractor.createApiKey("cHPxQW1tSreqmhbKbXoyxCjoYxcUYq1v09kN/WOq");
    Assertions.assertEquals("7073f141-6d6d-4ab7-aa9a-16ca6d7a32c4", apiKey.getClientId());
    Assertions.assertEquals("KOhjFxRirW/T2Q39Y6o=", apiKey.getToken().get());
  }
/*        - clientId: 7073f141-6d6d-4ab7-aa9a-16ca6d7a32c4
          hash: mXzA/tP9rdJ+qNSig7a70N6N6llWCBb7O1jzMLDVsRY=
          tenantId: 00000000-0000-0000-0000-000000000000

//  DIALOB_APIKEY=cHPxQW1tSreqmhbKbXoyxCjoYxcUYq1v09kN/WOq
//    DIALOB_APIKEY_HASH=mXzA/tP9rdJ+qNSig7a70N6N6llWCBb7O1jzMLDVsRY=
//    DIALOB_CLIENT_ID=7073f141-6d6d-4ab7-aa9a-16ca6d7a32c4
//    DIALOB_APIKEY_SALT=UxaJikUV5K0skFHIwkLObwnuL9O3umTk

 */

  @Test
  public void shouldTryCustomHeader() {
    RequestHeaderApiKeyExtractor extractor = new RequestHeaderApiKeyExtractor("MY-KEY");
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    assertNull(extractor.extract(request));

    verify(request).getAttribute(RequestHeaderApiKeyExtractor.REQUEST_APIKEY_ATTRIBUTE);
    verify(request).getHeader("MY-KEY");
    Mockito.verifyNoMoreInteractions(request);
  }


  @Test
  public void shouldReturnAlreadyExractedKey() {
    RequestHeaderApiKeyExtractor extractor = new RequestHeaderApiKeyExtractor();
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    ApiKey apiKey = ImmutableApiKey.of("123");
    when(request.getAttribute(RequestHeaderApiKeyExtractor.REQUEST_APIKEY_ATTRIBUTE)).thenReturn(apiKey);

    assertSame(apiKey, extractor.extract(request));

    verify(request).getAttribute(RequestHeaderApiKeyExtractor.REQUEST_APIKEY_ATTRIBUTE);
    Mockito.verifyNoMoreInteractions(request);
  }

  @Test
  public void shouldNotExtractInvalidKeyAndSetItTORequest() {
    RequestHeaderApiKeyExtractor extractor = new RequestHeaderApiKeyExtractor();
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    when(request.getHeader("x-api-key")).thenReturn("");

    assertNull(extractor.extract(request));

    verify(request).getAttribute(RequestHeaderApiKeyExtractor.REQUEST_APIKEY_ATTRIBUTE);
    verify(request).getHeader("x-api-key");
    Mockito.verifyNoMoreInteractions(request);
  }
  @Test
  public void shouldExtractKeyAndSetItTORequest() {
    RequestHeaderApiKeyExtractor extractor = new RequestHeaderApiKeyExtractor();
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    when(request.getHeader("x-api-key")).thenReturn("OZgCzYbPRs6PtNxJaP0TKR2nNk6VZrW4PbGZMn1D");

    ApiKey apiKey = extractor.extract(request);
    Assertions.assertEquals("399802cd-86cf-46ce-8fb4-dc4968fd1329", apiKey.getClientId());
    Assertions.assertEquals("Hac2TpVmtbg9sZkyfUM=", apiKey.getToken().get());

    verify(request).getAttribute(RequestHeaderApiKeyExtractor.REQUEST_APIKEY_ATTRIBUTE);
    verify(request).getHeader("x-api-key");
    verify(request).setAttribute(RequestHeaderApiKeyExtractor.REQUEST_APIKEY_ATTRIBUTE, ImmutableApiKey.builder()
      .clientId("399802cd-86cf-46ce-8fb4-dc4968fd1329").token("Hac2TpVmtbg9sZkyfUM=").build());
    Mockito.verifyNoMoreInteractions(request);
  }
}
