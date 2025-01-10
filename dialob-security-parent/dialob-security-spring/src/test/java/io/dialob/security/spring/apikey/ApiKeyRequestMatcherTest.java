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
package io.dialob.security.spring.apikey;

import io.dialob.security.key.ServletRequestApiKeyExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class ApiKeyRequestMatcherTest {

  @Test
  public void test() {
    ServletRequestApiKeyExtractor extractor = Mockito.mock(ServletRequestApiKeyExtractor.class);
    when(extractor.getHeader()).thenReturn("my-header");
    ApiKeyRequestMatcher matcher = new ApiKeyRequestMatcher(extractor);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    Assertions.assertFalse(matcher.matches(request));
    verify(request).getHeader("my-header");
    Mockito.reset(request);

    when(request.getHeader("my-header")).thenReturn("");
    Assertions.assertTrue(matcher.matches(request));

    verify(request).getHeader("my-header");
    verify(extractor).getHeader();
    verifyNoMoreInteractions(extractor);
  }
}
