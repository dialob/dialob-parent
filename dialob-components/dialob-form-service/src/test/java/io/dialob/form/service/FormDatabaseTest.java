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
package io.dialob.form.service;

import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.common.Constants;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@EnableCaching
public class FormDatabaseTest {

  String tenantId = "123";

  static FormDatabase formDatabaseMock = mock(FormDatabase.class);

  static ConcurrentMapCache cache = new ConcurrentMapCache(Constants.FORM_CACHE_NAME);

  @Configuration(proxyBeanMethods = false)
  @ImportResource("classpath:dialob-form-service-cache-context.xml")
  public static class FormDatabaseTestConfiguration {
    @Bean
    public FormDatabase formDatabase() {
      return formDatabaseMock;
    }

    @Bean
    public CacheManager cacheManager() {
      SimpleCacheManager cacheManager = new SimpleCacheManager();
      cacheManager.setCaches(Arrays.asList(cache));
      return cacheManager;
    }
  }

  @Autowired
  private FormDatabase formDatabase;

  private CacheManager cacheManager;


  @BeforeEach
  public void flush() {
    cache.clear();
    Mockito.reset(formDatabaseMock);
  }

  @Test
  public void shouldFetchFromServiceOnce() {
    Form document = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test").build()).build();
    when(formDatabaseMock.findOne(tenantId, "1","2")).thenReturn(document);
    assertSame(document, formDatabase.findOne(tenantId, "1","2"));
    assertSame(document, formDatabase.findOne(tenantId, "1","2"));
    assertSame(document, formDatabase.findOne(tenantId, "1","2"));
    verify(formDatabaseMock, times(1)).findOne(tenantId, "1","2");
  }

  @Test
  public void shouldPassException() {
    Assertions.assertThatExceptionOfType(DocumentNotFoundException.class).isThrownBy(() -> {
      when(formDatabaseMock.findOne(tenantId, "1","2")).thenThrow(DocumentNotFoundException.class);
      formDatabase.findOne(tenantId, "1","2");
    });
  }

  @Test
  public void saveShouldPutDocumentInCache() {
    Form document = ImmutableForm.builder().id("id-1").rev("rev-1").metadata(ImmutableFormMetadata.builder().label("test").build()).build();

    when(formDatabaseMock.findOne(tenantId, "id-1")).thenThrow(DocumentNotFoundException.class);
    when(formDatabaseMock.findOne(tenantId, "id-1","rev-1")).thenThrow(DocumentNotFoundException.class);
    when(formDatabaseMock.save(eq(tenantId), any())).thenAnswer(AdditionalAnswers.returnsSecondArg());
    when(formDatabaseMock.exists(tenantId, "id-1")).thenReturn(false,true, false);

    // exists are cached
    assertFalse(formDatabase.exists(tenantId, "id-1"));
    assertFalse(formDatabase.exists(tenantId, "id-1"));
    assertSame(document, formDatabase.save(tenantId, document));
    // save flushed exists
    assertTrue(formDatabase.exists(tenantId, "id-1"));
    assertTrue(formDatabase.exists(tenantId, "id-1"));

    // save puts with and without revision
    assertSame(document, formDatabase.findOne(tenantId, "id-1"));
    assertSame(document, formDatabase.findOne(tenantId, "id-1","rev-1"));

    // delete flushed exists
    formDatabase.delete(tenantId, "id-1");
    assertFalse(formDatabase.exists(tenantId, "id-1"));
    assertFalse(formDatabase.exists(tenantId, "id-1"));
    // delete flushed forms
    Assertions.assertThatThrownBy(() -> formDatabase.findOne(tenantId, "id-1")).isInstanceOf(DocumentNotFoundException.class);
    Assertions.assertThatThrownBy(() -> formDatabase.findOne(tenantId, "id-1","rev-1")).isInstanceOf(DocumentNotFoundException.class);

    verify(formDatabaseMock, times(1)).findOne(tenantId, "id-1");
    verify(formDatabaseMock, times(1)).findOne(tenantId, "id-1","rev-1");
    verify(formDatabaseMock, times(1)).save(tenantId, document);
    verify(formDatabaseMock, times(3)).exists(tenantId, "id-1");
    verify(formDatabaseMock, times(1)).delete(tenantId, "id-1");
    verifyNoMoreInteractions(formDatabaseMock);
  }

}
