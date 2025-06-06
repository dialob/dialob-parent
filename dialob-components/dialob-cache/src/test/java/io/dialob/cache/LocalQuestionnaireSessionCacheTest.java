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
package io.dialob.cache;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.common.Constants;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LocalQuestionnaireSessionCacheTest {

  @Test
  void shouldBeEmptyByDefault() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    assertEquals(0, cache.size());
  }

  @Test
  void shouldReturnNullWhenQuestionnaireIsNotFound() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    Assertions.assertNull(cache.get("q1"));
    Assertions.assertNull(cache.get("q1", Questionnaire.class));
    Assertions.assertNotNull(cache.get("q1", () -> "1"));
  }

  @Test
  void shouldEvictSessionWhenThereIsPersistenceConflict() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    Function<QuestionnaireSession,QuestionnaireSession> beforeCloseCallback = mock(Function.class);
    when(beforeCloseCallback.apply(any())).thenThrow(DocumentConflictException.class);
    var q = mock(QuestionnaireSession.class);
    when(q.getSessionId()).thenReturn(Optional.of("123"));
    when(q.getTenantId()).thenReturn("T12");
    when(q.isActive()).thenReturn(true);
    cache.put(q);
    Assertions.assertNotNull(cache.get("123"));
    cache.evict("123", beforeCloseCallback);
    Assertions.assertNull(cache.get("123"));
  }

  @Test
  void shouldWrapSameObject() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    QuestionnaireSession session = mock();
    when(session.getSessionId()).thenReturn(Optional.of("123"));
    assertNull(cache.get("123"));
    var wrapper = cache.putIfAbsent("123", session);
    assertNotNull(wrapper);
    assertSame(session, wrapper.get());
    assertNotNull(cache.get("123"));

  }

  @Test
  void doesNotLikeStrangeTypes() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    Assertions.assertThrows(IllegalArgumentException.class, () -> cache.putIfAbsent("213", null));
  }
}
