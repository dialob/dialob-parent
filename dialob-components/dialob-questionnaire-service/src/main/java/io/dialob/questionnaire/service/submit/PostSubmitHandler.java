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
package io.dialob.questionnaire.service.submit;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.AnswerSubmitHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

@Slf4j
public class PostSubmitHandler implements AnswerSubmitHandler {

  private ClientHttpRequestFactory requestFactory;

  public void submit(@NonNull AnswerSubmitHandler.Settings submitHandlerSettings, @NonNull Questionnaire document) {
    String submitUrl = document.getMetadata().getSubmitUrl();
    if (StringUtils.isBlank(submitUrl)) {
      LOGGER.info("Form submit endpoint not defined for questionnaire {}", document.getId());
      return;
    }
    try {
      URL url = new URL(submitUrl);
      final HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.set(HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_JSON_VALUE);

      final HttpEntity<?> httpEntity = new HttpEntity<>(document, httpHeaders);
      final String userInfo = url.getUserInfo();
      String username = null;
      String password = null;
      String[] credentials = null;
      if (StringUtils.isNotBlank(userInfo)) {
        credentials = userInfo.split(":", 2);
      }
      if (credentials != null && credentials.length == 2) {
        username = credentials[0];
        password = credentials[1];
      }
      final ResponseEntity<String> response = createRestTemplate(username, password).exchange(submitUrl, HttpMethod.POST, httpEntity, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        LOGGER.debug("Successfully POSTed questionnaire {} answers to {}, response {}",
            document.getId(), submitUrl, response.getStatusCode());
      } else {
        String entity = null;
        try {
          entity = response.getBody();
        } catch (Exception e) {
          LOGGER.debug("Error response content could not be read.", e);
        }
        LOGGER.error("There was a problem POSTing questionnaire {} answers to {}, response {}, response content '{}'",
            document.getId(), submitUrl, response.getStatusCode(), entity);
      }
    } catch (RestClientException|MalformedURLException e) {
      LOGGER.error(String.format("Failed to POST questionnaire %s results to %s",
          document.getId(), submitUrl), e);
    }
  }

  public RestTemplate createRestTemplate(String username, String password) {
    final RestTemplate restTemplate = requestFactory != null ? new RestTemplate(requestFactory) : new RestTemplate();
    if (username != null && password != null) {
      restTemplate.setInterceptors(Collections.singletonList(new BasicAuthenticationInterceptor(username, password)));
    }
    return restTemplate;
  }

  public ClientHttpRequestFactory getRequestFactory() {
    return requestFactory;
  }

  public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
    this.requestFactory = requestFactory;
  }
}
