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
package io.dialob.db.dialob.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class DialobApiTemplate {

  private final RestTemplate restTemplate;

  private final URI uri;

  private final DialobApiDbSettings settings;

  public DialobApiTemplate(RestTemplate restTemplate, DialobApiDbSettings settings) {
    this.restTemplate = restTemplate;
    this.settings = settings;
    this.uri = settings.getUri();
  }

  public <T> T findOne(@NonNull String resource, @NonNull String id, String rev, Class<T> entityType) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri).pathSegment(resource, id);
    if (StringUtils.isNotBlank(rev)) {
      builder = builder.queryParam("rev",rev);
    }
    URI url = builder.build().toUri();
    return doRequest(url, HttpMethod.GET, null, entityType);
  }

  @NonNull
  public <T> T save(@NonNull String resource, String id, @NonNull T document) {
    HttpMethod httpMethod = HttpMethod.POST;
    UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri).pathSegment(resource);
    if (StringUtils.isNotBlank(id)) {
      builder = builder.pathSegment(id);
      httpMethod = HttpMethod.PUT;
    }
    URI url = builder.build().toUri();
    Class<?> entityType = document.getClass();
    return doRequest(url, httpMethod, document, (Class<T>) entityType);
  }

  protected <E> ResponseEntity<E> handleResponse(ResponseEntity<E> responseEntity) {
    return responseEntity;
  }

  protected <T> T doRequest(URI url, HttpMethod httpMethod, T document, Class<T> responseType) {
    return handleResponse(restTemplate.exchange(url, httpMethod, httpEntity(document), responseType)).getBody();
  }

  protected HttpEntity httpEntity(Object document) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    DialobApiDbSettings.Authentication authentication = settings.getAuthentication();
    if (authentication != null) {
      String apikey = authentication.getApikey();
      httpHeaders.set(HttpHeaders.AUTHORIZATION, "ApiKey " + apikey);
    }
    if (document == null) {
      return new HttpEntity(decorateHttpHeaders(httpHeaders));
    }
    httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    return new HttpEntity(document, decorateHttpHeaders(httpHeaders));
  }

  protected MultiValueMap<String, String> decorateHttpHeaders(HttpHeaders httpHeaders) {
    return httpHeaders;
  }

  public RestTemplate getRestTemplate() {
    return restTemplate;
  }
}
