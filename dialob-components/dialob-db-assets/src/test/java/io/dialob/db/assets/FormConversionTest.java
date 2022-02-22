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
package io.dialob.db.assets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.db.assets.repository.AssetRepository;
import io.dialob.db.assets.repository.GenericAssetRepository;
import io.dialob.db.assets.serialization.AssetFormDeserializer;
import io.dialob.db.assets.serialization.AssetFormMetadataRowDeserializer;
import io.dialob.db.assets.serialization.AssetFormSerializer;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.settings.DialobSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FormConversionTest {

  private FormDatabase formDatabase;

  private RestTemplate restTemplate;

  String tenantId = null;

  @BeforeEach
  public void setUp() {
    DialobSettings settings = new DialobSettings();
    settings.getAssets().getService().setUrl("http://localhost:8081/rest/api/assets");

    ObjectMapper objectMapper = new ObjectMapper();

    restTemplate = mock(RestTemplate.class);
    AssetRepository assetRepository = new GenericAssetRepository(restTemplate, settings, objectMapper);

    AssetFormSerializer assetFormSerializer = new AssetFormSerializer(objectMapper);
    AssetFormDeserializer assetFormDeserializer = new AssetFormDeserializer(objectMapper);
    AssetFormMetadataRowDeserializer assetFormMetadataRowDeserializer = new AssetFormMetadataRowDeserializer(objectMapper);

    this.formDatabase = new AssetFormDatabase(assetRepository, assetFormSerializer, assetFormDeserializer, assetFormMetadataRowDeserializer);
  }

  @Test
  public void get() throws JsonProcessingException {
    ResponseEntity responseEntity = mock(ResponseEntity.class);
    when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
    when(restTemplate.exchange(eq("http://localhost:8081/rest/api/assets?name=dialobTestForm&type=DIALOB"), eq(HttpMethod.GET), any(HttpEntity.class),
      eq(GenericAssetRepository.PARAMETERIZED_TYPE_REFERENCE))).thenReturn(responseEntity);
    when(responseEntity.getBody()).thenReturn(Collections.emptyList());

    Assertions.assertThrows(DocumentNotFoundException.class, () -> formDatabase.findOne(tenantId, "dialobTestForm"));
    verify(restTemplate).exchange(eq("http://localhost:8081/rest/api/assets?name=dialobTestForm&type=DIALOB"), eq(HttpMethod.GET), any(HttpEntity.class),
      eq(GenericAssetRepository.PARAMETERIZED_TYPE_REFERENCE));
    verify(responseEntity).getStatusCode();
    verify(responseEntity).getStatusCodeValue();
    verify(responseEntity).getBody();
    verifyNoMoreInteractions(restTemplate, responseEntity);
  }

  //@Test
  public void post() throws JsonProcessingException {
    formDatabase.save(tenantId, ImmutableForm.builder()
        .id("questions").metadata(ImmutableFormMetadata.builder().build()).build()
        );
  }

  //@Test
  public void delete() throws JsonProcessingException {
    formDatabase.save(tenantId, ImmutableForm.builder()
        .id("questions1").metadata(ImmutableFormMetadata.builder().build()).build()
        );

    formDatabase.delete(tenantId, "questions1");
  }

  //@Test
  public void getmetadata() throws JsonProcessingException {
    formDatabase.save(tenantId, ImmutableForm.builder()
        .id("questions2").metadata(ImmutableFormMetadata.builder().build()).build()
        );
    formDatabase.findAllMetadata(tenantId, null, e -> {});
  }

}
