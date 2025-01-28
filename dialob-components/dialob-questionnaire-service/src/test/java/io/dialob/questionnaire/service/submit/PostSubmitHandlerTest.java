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
package io.dialob.questionnaire.service.submit;

import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class PostSubmitHandlerTest {

  @Test
  void shouldMakePostToURL() throws Exception {
    AnswerSubmitHandler.Settings submitHandlerSettings = mock(AnswerSubmitHandler.Settings.class);
    PostSubmitHandler postSubmitHandler  = new PostSubmitHandler();

    final ClientHttpRequestFactory requestFactory = mock(ClientHttpRequestFactory.class);
    final ClientHttpRequest request = mock(ClientHttpRequest.class);
    final ClientHttpResponse response = mock(ClientHttpResponse.class);
    final HttpHeaders httpHeaders = mock(HttpHeaders.class);
    final HttpHeaders responseHttpHeaders = mock(HttpHeaders.class);
    final OutputStream body = mock(OutputStream.class);
    when(requestFactory.createRequest(new URI("http://localhost:8080/here"), HttpMethod.POST)).thenReturn(request);
    when(request.getHeaders()).thenReturn(httpHeaders);
    when(request.getBody()).thenReturn(body);
    when(request.execute()).thenReturn(response);
    when(response.getStatusCode()).thenReturn(HttpStatus.OK);
    when(response.getHeaders()).thenReturn(responseHttpHeaders);


    postSubmitHandler.setRequestFactory(requestFactory);
    Questionnaire document = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("").submitUrl("http://localhost:8080/here").build()).build();
    postSubmitHandler.submit(submitHandlerSettings, document);

    verify(requestFactory).createRequest(new URI("http://localhost:8080/here"), HttpMethod.POST);
    verify(httpHeaders).setAccept(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.valueOf("application/*+json"), MediaType.ALL));
    verify(httpHeaders).setContentType(MediaType.APPLICATION_JSON);
    verify(httpHeaders, times(2)).getContentType();
//    HashMap<String, List<String>> map = Maps.newHashMap();
//    map.put("Content-Type",Arrays.asList("application/json"));
    verify(httpHeaders).put("Content-Type", List.of("application/json"));
    verify(httpHeaders).getContentLength();

    verifyNoMoreInteractions(httpHeaders, requestFactory);
  }

  @Test
  void shouldAddBasicAuthenticationIfCredentialsDefinedOnURL() throws Exception {
    AnswerSubmitHandler.Settings submitHandlerSettings = mock(AnswerSubmitHandler.Settings.class);
    PostSubmitHandler postSubmitHandler  = new PostSubmitHandler();

    final ClientHttpRequestFactory requestFactory = mock(ClientHttpRequestFactory.class);
    final ClientHttpRequest request = mock(ClientHttpRequest.class);
    final ClientHttpResponse response = mock(ClientHttpResponse.class);
    final HttpHeaders httpHeaders = mock(HttpHeaders.class);
    final HttpHeaders responseHttpHeaders = mock(HttpHeaders.class);
    final OutputStream body = mock(OutputStream.class);
    when(requestFactory.createRequest(new URI("http://test:pass@localhost:8080/here"), HttpMethod.POST)).thenReturn(request);
    when(request.getHeaders()).thenReturn(httpHeaders);
    when(request.getBody()).thenReturn(body);
    when(request.execute()).thenReturn(response);
    when(response.getStatusCode()).thenReturn(HttpStatus.OK);
    when(response.getHeaders()).thenReturn(responseHttpHeaders);


    postSubmitHandler.setRequestFactory(requestFactory);
    Questionnaire document = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("").submitUrl("http://test:pass@localhost:8080/here").build()).build();
    postSubmitHandler.submit(submitHandlerSettings, document);

    verify(requestFactory).createRequest(new URI("http://test:pass@localhost:8080/here"), HttpMethod.POST);
    verify(httpHeaders).addAll("Accept", List.of("text/plain, application/json, application/*+json, */*"));
    verify(httpHeaders).addAll("Content-Type", List.of("application/json"));
    verify(httpHeaders).addAll("Content-Length", List.of("80"));
    verify(body).write("{\"metadata\":{\"status\":\"NEW\",\"submitUrl\":\"http://test:pass@localhost:8080/here\"}}".getBytes());
    verify(httpHeaders).addAll("Authorization", List.of("Basic dGVzdDpwYXNz"));
    verify(httpHeaders).getContentLength();
    verify(httpHeaders).setContentLength(80);
    verifyNoMoreInteractions(httpHeaders, requestFactory);
  }

}
