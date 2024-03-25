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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

class LocalQuestionnaireSessionCacheTest {

  @Test
  public void shouldBeEmptyByDefault() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    assertEquals(0, cache.size());
  }

  @Test
  public void shouldReturnNullWhenQuestionnaireIsNotFound() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    Assertions.assertNull(cache.get("q1"));
    Assertions.assertNull(cache.get("q1", Questionnaire.class));
    Assertions.assertNotNull(cache.get("q1", () -> "1"));
  }

  @Test
  public void shouldEvictSessionWhenThereIsPersistenceConflict() {
    LocalQuestionnaireSessionCache cache = new LocalQuestionnaireSessionCache(Constants.SESSION_CACHE_NAME);
    Function<QuestionnaireSession,QuestionnaireSession> beforeCloseCallback = Mockito.mock(Function.class);
    Mockito.when(beforeCloseCallback.apply(any())).thenThrow(DocumentConflictException.class);
    var q = Mockito.mock(QuestionnaireSession.class);
    Mockito.when(q.getSessionId()).thenReturn(Optional.of("123"));
    Mockito.when(q.getTenantId()).thenReturn("T12");
    Mockito.when(q.isActive()).thenReturn(true);
    cache.put(q);
    Assertions.assertNotNull(cache.get("123"));
    cache.evict("123", beforeCloseCallback);
    Assertions.assertNull(cache.get("123"));
  }

}
