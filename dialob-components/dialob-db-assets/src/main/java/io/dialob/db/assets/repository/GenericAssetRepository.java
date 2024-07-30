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
package io.dialob.db.assets.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.spi.exceptions.DatabaseException;
import io.dialob.settings.DialobSettings;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;


public class GenericAssetRepository implements AssetRepository {
  private static final String REV_LATEST = "LATEST";
  private static final String QUERY_TYPE_NAME = "name";
  public static final ParameterizedTypeReference<List<ObjectNode>> PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<List<ObjectNode>>() { };

  private final RestTemplate restTemplate;
  private final DialobSettings.DialobAssetsServiceSettings settings;
  private final ObjectMapper objectMapper;

  public GenericAssetRepository(RestTemplate restTemplate, DialobSettings settings, ObjectMapper objectMapper) {
    super();
    this.restTemplate = restTemplate;
    this.settings = settings.getAssets().getService();
    this.objectMapper = objectMapper;
  }

  @NonNull
  @Override
  public AssetBuilder createBuilder() {
    return new AssetBuilder() {
      private String document;
      @NonNull
      @Override
      public AssetBuilder document(@NonNull String document) {
        this.document = document;
        return this;
      }
      @NonNull
      @Override
      public ObjectNode build() {
        Assert.notNull(document, "document can't be null!");
        return postExchange(document);
      }
    };
  }

  @NonNull
  @Override
  public AssetQuery createQuery() {
    Map<String, String> params = new HashMap<>();
    params.put("type", "DIALOB");

    return new AssetQuery() {
      @NonNull
      @Override
      public AssetQuery rev(@NonNull String rev) {
        if(!REV_LATEST.equalsIgnoreCase(rev)) {
          params.put("rev", rev);
        }
        return this;
      }
      @NonNull
      @Override
      public AssetQuery metadata() {
        params.put("criteriaType", "METADATA");
        return this;
      }
      @NonNull
      @Override
      public AssetQuery id(@NonNull String id) {
        params.put(QUERY_TYPE_NAME, id);
        return this;
      }
      @Override
      public Optional<ObjectNode> get() {
        Assert.isTrue(!StringUtils.isEmpty(params.get(QUERY_TYPE_NAME)), "id must be defined!");

        List<ObjectNode> result = getExchange(params);
        if(result.isEmpty()) {
          return Optional.empty();
        } else if(result.size() > 1) {
          throw new DatabaseException(String.format("expecting one or zero documents but found: %s for criteria: %s", result.size(), params.toString()));
        }
        return Optional.of(result.get(0));
      }
      @NonNull
      @Override
      public List<ObjectNode> list() {
        return getExchange(params);
      }
      @Override
      public void delete() {
        Assert.isTrue(!StringUtils.isEmpty(params.get(QUERY_TYPE_NAME)), "id must be defined!");
        deletExchange(params);
      }
    };
  }

  protected void deletExchange(Map<String, String> params) {
    ResponseEntity<List<ObjectNode>> response;
    try {
      UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(settings.getUrl());
      params.entrySet().stream()
      .filter(entry -> entry.getValue() != null)
      .forEach(entry -> uri.queryParam(entry.getKey(), entry.getValue()));
      HttpEntity<List<JsonNode>> requestEntity = new HttpEntity<>(createHeaders());

      response = restTemplate.exchange(uri.toUriString(), HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<List<ObjectNode>>() {});
      Assert.isTrue(response.getStatusCode().is2xxSuccessful(), "Asset status was: " + response.getStatusCodeValue() + " but expecting 200!");
    } catch(Exception e) {
      throw new DatabaseException(String.format("failed to get assets: %s! ", e.getMessage()));
    }
  }

  protected List<ObjectNode> getExchange(Map<String, String> params) {
    ResponseEntity<List<ObjectNode>> response;
    try {
      UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(settings.getUrl());
      params.entrySet().stream()
      .filter(entry -> entry.getValue() != null)
      .forEach(entry -> uri.queryParam(entry.getKey(), entry.getValue()));
      HttpEntity<List<JsonNode>> requestEntity = new HttpEntity<>(createHeaders());

      response = restTemplate.exchange(uri.toUriString(), HttpMethod.GET, requestEntity, PARAMETERIZED_TYPE_REFERENCE);
      Assert.isTrue(response.getStatusCode().is2xxSuccessful(), "Asset status was: " + response.getStatusCodeValue() + " but expecting 200!");
      return response.getBody();
    } catch(Exception e) {
      throw new DatabaseException(String.format("failed to get assets: %s! ", e.getMessage()), e);
    }
  }

  protected ObjectNode postExchange(String document) {
    ResponseEntity<List<ObjectNode>> response;
    try {
      ObjectNode asset = objectMapper.createObjectNode();
      asset.put("type", "DIALOB");
      asset.put("content", document);
      HttpEntity<String> requestEntity = new HttpEntity<>(asset.toString(), createHeaders());

      response = restTemplate.exchange(settings.getUrl(), HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<ObjectNode>>() {});
      Assert.isTrue(response.getStatusCode().is2xxSuccessful(), "Asset status was: " + response.getStatusCodeValue() + " but expecting 200!");
      return response.getBody().get(0);
    } catch (Exception e) {
      throw new DatabaseException(String.format("failed to handle asset request: %s! ", e.getMessage()));
    }
  }

  protected HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    if(!StringUtils.isEmpty(settings.getAuthorization())) {
      headers.set("Authorization", settings.getAuthorization());
    }
    return headers;
  }
}
