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
package io.dialob.security.spring.apikey.filter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dialob.security.ErrorsResponse;
import io.dialob.security.spring.apikey.ApiKeyAuthenticationException;
import io.dialob.security.spring.filter.ApiKeyAuthenticationEntryPoint;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.DelegatingServletOutputStream;
import org.springframework.security.core.AuthenticationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.verify;

class ApiKeyAuthenticationEntryPointTest {

  @Test
  void shouldMakeJsonErrorResponse() throws IOException, ServletException {
    final ObjectMapper objectMapper = new ObjectMapper()
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
      .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
    ApiKeyAuthenticationEntryPoint entryPoint = new ApiKeyAuthenticationEntryPoint();
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    AuthenticationException authException = new ApiKeyAuthenticationException("Invalid key");;

    OutputStream outputStream = new ByteArrayOutputStream(10000);
    ;
    Mockito.when(response.getOutputStream()).thenReturn(new DelegatingServletOutputStream(outputStream));

    entryPoint.commence(request, response, authException);

    verify(response).setStatus(403);
    verify(response).getOutputStream();
    verify(response).setHeader("Content-Type", "application/json");

    final String content = outputStream.toString();
    ErrorsResponse errorsResponse = objectMapper.readValue(content, ErrorsResponse.class);

    Assertions.assertEquals("Forbidden",errorsResponse.getError());
    Assertions.assertEquals("Invalid key",errorsResponse.getMessage());
    Assertions.assertEquals((Integer) 403, errorsResponse.getStatus());
    Assertions.assertNotNull(errorsResponse.getTimestamp());
    Mockito.verifyNoMoreInteractions(request, response);
  }
}
