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
package io.dialob.questionnaire.service;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AbstractCacheTest.TestConfiguration.class})
@EnableCaching
class QuestionnaireDatabaseTest extends AbstractCacheTest {

  private final String tenantId = "t-123";

  @Inject
  public QuestionnaireDatabase questionnaireDatabase;

  @Test
  void shouldStoreQuestionnaireIntoCacheIfIsNotThereAlready() {
    final Cache cache = setupCache("questionnaireCache");
    assertTrue(AopUtils.isAopProxy(questionnaireDatabase));
    QuestionnaireDatabase targetService = unwrap(questionnaireDatabase);

    Questionnaire questionnaire = mock(Questionnaire.class);
    doReturn(questionnaire).when(targetService).findOne(tenantId, "123");
    when(cache.get(Arrays.asList("q","t-123", "123",null))).thenReturn(null);

    questionnaireDatabase.findOne(tenantId, "123");

    verify(cache).getName();
    verify(cache).get(Arrays.asList("q","t-123", "123",null));
    verify(targetService).findOne(tenantId, "123");
    verify(cache).put(Arrays.asList("q","t-123", "123",null), questionnaire);
    verifyNoMoreInteractions(cache, targetService);
  }

  @Test
  void shouldReturnQuestionnaireFromCache() {
    final Cache cache = setupCache("questionnaireCache");
    Questionnaire questionnaire = mock(Questionnaire.class);
    when(cache.get(Arrays.asList("q","t-123", "123",null))).thenReturn(() -> questionnaire);

    questionnaireDatabase.findOne(tenantId, "123");

    verify(cache).getName();
    verify(cache).get(Arrays.asList("q","t-123", "123",null));
    verifyNoMoreInteractions(cache, unwrap(questionnaireDatabase));
  }

  @Test
  void shouldCheckQuestionnaireFromCache() {
    final Cache cache = setupCache("questionnaireCache");

    assertTrue(AopUtils.isAopProxy(questionnaireDatabase));
    QuestionnaireDatabase targetService = unwrap(questionnaireDatabase);

    doReturn(true).when(targetService).exists("t-123", "123");

    questionnaireDatabase.exists(tenantId, "123");

    verify(cache).getName();
    verify(cache).get(Arrays.asList("e","t-123", "123",null));
    verify(cache).put(Arrays.asList("e","t-123", "123",null), true);
    verify(targetService).exists("t-123", "123");
    verifyNoMoreInteractions(cache, targetService);
  }

  @Test
  void saveShouldUpdateObjectInCacheWithReturnValue() {
    final Cache cache = setupCache("questionnaireCache");
    final Questionnaire questionnaireIn = mock(Questionnaire.class);
    final Questionnaire questionnaireOut = mock(Questionnaire.class);
    when(questionnaireIn.getId()).thenReturn("123");
    when(questionnaireIn.getRev()).thenReturn("r-122");
    when(questionnaireOut.getId()).thenReturn("123");
    when(questionnaireOut.getRev()).thenReturn("r-123");

    assertTrue(AopUtils.isAopProxy(questionnaireDatabase));
    QuestionnaireDatabase targetService = unwrap(questionnaireDatabase);
    when(targetService.save(tenantId, questionnaireIn))
      .thenReturn(questionnaireOut);

    questionnaireDatabase.save(tenantId, questionnaireIn);

    verify(cache, times(4)).getName();
    verify(cache).evictIfPresent(Arrays.asList("q","t-123", "123",null));

    verify(cache).put(Arrays.asList("q","t-123", "123",null), questionnaireOut);
    verify(cache).put(Arrays.asList("q","t-123", "123","r-123"), questionnaireOut);
    verify(cache).evict(Arrays.asList("e","t-123", "123",null));
    verify(questionnaireOut, times(3)).getId();
    verify(targetService).save(tenantId, questionnaireIn);
    verifyNoMoreInteractions(cache, targetService);
  }

  @Test
  void saveShouldReloadCacheIfSaveEndsInConflict() {
    final Cache cache = setupCache("questionnaireCache");
    final Questionnaire questionnaireIn = mock(Questionnaire.class, "q1");
    final Questionnaire questionnaireOut = mock(Questionnaire.class, "q2");
    when(questionnaireIn.getId()).thenReturn("123");
    when(questionnaireIn.getRev()).thenReturn("r-122");
    when(questionnaireOut.getId()).thenReturn("123");
    when(questionnaireOut.getRev()).thenReturn("r-123");

    QuestionnaireDatabase targetService = unwrap(questionnaireDatabase);
    when(targetService.save(tenantId, questionnaireIn))
      .thenAnswer(i -> questionnaireOut);
    when(targetService.save(tenantId, questionnaireOut))
      .thenThrow(DocumentConflictException.class);

    assertSame(questionnaireOut, questionnaireDatabase.save(tenantId, questionnaireIn));
    assertThrows(DocumentConflictException.class, () -> questionnaireDatabase.save(tenantId, questionnaireOut));

    var order = inOrder(cache, targetService);
    order.verify(cache, times(4)).getName();
    order.verify(cache).evictIfPresent(Arrays.asList("q","t-123", "123",null));
    order.verify(targetService).save(tenantId, questionnaireIn);
    order.verify(cache).put(Arrays.asList("q","t-123", "123",null), questionnaireOut);
    order.verify(cache).put(Arrays.asList("q","t-123", "123","r-123"), questionnaireOut);
    order.verify(cache).evict(Arrays.asList("e","t-123", "123",null));
    order.verify(cache, times(4)).getName();
    order.verify(cache).evictIfPresent(Arrays.asList("q","t-123", "123",null));
    order.verify(targetService).save(tenantId, questionnaireOut);
    order.verifyNoMoreInteractions();

    // Do not remove this! Verification above does not cover all interactions.
    verifyNoMoreInteractions(cache, targetService);
  }

  @Test
  void deleteShouldEvictQuestionnaireFromCache() {
    final Cache cache = setupCache("questionnaireCache");
    assertTrue(AopUtils.isAopProxy(questionnaireDatabase));
    QuestionnaireDatabase targetService = unwrap(questionnaireDatabase);
    when(targetService.exists(tenantId, "123")).thenReturn(true).thenReturn(false);
    when(targetService.delete(tenantId, "123")).thenReturn(true);

    assertTrue(questionnaireDatabase.exists(tenantId, "123"));
    assertTrue(questionnaireDatabase.delete(tenantId, "123"));
    assertFalse(questionnaireDatabase.exists(tenantId, "123"));

    var order = inOrder(cache, targetService);

    order.verify(cache).getName();
    order.verify(cache).get(Arrays.asList("e","t-123", "123",null));
    order.verify(targetService).exists(tenantId, "123");
    order.verify(cache).put(eq(Arrays.asList("e","t-123", "123",null)), eq(true));
    order.verify(cache).getName();
    order.verify(targetService).delete(tenantId, "123");
    order.verify(cache).clear();
    order.verify(cache).getName();
    order.verify(cache).get(Arrays.asList("e","t-123", "123",null));
    order.verify(targetService).exists(tenantId, "123");
    order.verify(cache).put(eq(Arrays.asList("e","t-123", "123",null)), eq(false));
    order.verifyNoMoreInteractions();

    // Do not remove this! Verification above does not cover all interactions.
    verifyNoMoreInteractions(cache, targetService);
  }

}
