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
package io.dialob.questionnaire.service.session;

import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.questionnaire.service.AbstractCacheTest;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AbstractCacheTest.TestConfiguration.class})
@EnableCaching
class QuestionnaireSessionServiceTest extends AbstractCacheTest {

  @Inject
  public QuestionnaireSessionService questionnaireSessionService;

  @Inject
  public QuestionnaireSessionSaveService questionnaireSessionSaveService;


  @Test
  void findOneShouldCacheNonNullResult() {
    var cache = setupCache("sessionCache");
    var targetService = unwrap(questionnaireSessionService);

    when(cache.get("findOneShouldCacheNonNullResult")).thenReturn(null);
    QuestionnaireSession session = mock();
    when(targetService.findOne("findOneShouldCacheNonNullResult")).thenReturn(session);

    questionnaireSessionService.findOne("findOneShouldCacheNonNullResult");

    verify(cache).getName();
    verify(cache).get("findOneShouldCacheNonNullResult");
    verify(cache).put("findOneShouldCacheNonNullResult", session);
    verify(targetService).findOne("findOneShouldCacheNonNullResult");
    verifyNoMoreInteractions(cache, targetService);
  }

  @Test
  void findOneShouldNotCacheNullResult() {
    var cache = setupCache("sessionCache");
    var targetService = unwrap(questionnaireSessionService);
    Mockito.reset(targetService);

    when(cache.get("findOneShouldNotCacheNullResult")).thenReturn(null);
    when(targetService.findOne("findOneShouldNotCacheNullResult")).thenReturn(null);

    assertNull(questionnaireSessionService.findOne("findOneShouldNotCacheNullResult"));

    verify(cache).getName();
    verify(cache).get("findOneShouldNotCacheNullResult");
    verify(targetService).findOne("findOneShouldNotCacheNullResult");
    verifyNoMoreInteractions(cache, targetService);
  }

  @Test
  void findOneWithOpenFalseShouldReturnNullIfItemNotInCache() {
    Cache cache = setupCache("sessionCache");
    QuestionnaireSessionService targetService = unwrap(questionnaireSessionService);

    when(cache.get("findOneWithOpenFalseShouldReturnNullIfItemNotInCache")).thenReturn(null);
    assertNull(questionnaireSessionService.findOne("findOneWithOpenFalseShouldReturnNullIfItemNotInCache", false));

    verify(cache).getName();
    verify(cache).get("findOneWithOpenFalseShouldReturnNullIfItemNotInCache");
    verify(targetService).findOne("findOneWithOpenFalseShouldReturnNullIfItemNotInCache", false);
    verifyNoMoreInteractions(cache, targetService);
  }

  @Test
  void saveShouldCacheResult() {
    var cache = setupCache("sessionCache");
    QuestionnaireSessionSaveService targetService = unwrap(questionnaireSessionSaveService);

    QuestionnaireSession session = mock();
    QuestionnaireSession sessionOut = mock();
    when(sessionOut.getSessionId()).thenReturn(Optional.of("saveShouldCacheResult"));
    when(targetService.save(session)).thenReturn(sessionOut);

    assertSame(sessionOut, questionnaireSessionSaveService.save(session));

    var inOrder = Mockito.inOrder(cache, targetService, session);
    System.out.println(mockingDetails(session).printInvocations());

    inOrder.verify(cache, times(1)).getName();
//    inOrder.verify(cache).evictIfPresent(Optional.empty());
    inOrder.verify(targetService).save(session);
    inOrder.verify(cache).put(Optional.of("saveShouldCacheResult"), sessionOut);
    inOrder.verifyNoMoreInteractions();

//    verify(session, atLeastOnce()).getSessionId();
    verifyNoMoreInteractions(cache, targetService, session);
  }


  @Test
  void saveShouldEvictSessionWhenSaveFails() {
    var cache = setupCache("sessionCache");
    QuestionnaireSessionSaveService targetService = unwrap(questionnaireSessionSaveService);

    QuestionnaireSession session = mock();
    QuestionnaireSession sessionOut = mock();
    when(session.getSessionId()).thenReturn(Optional.of("saveShouldCacheResult"));
    when(sessionOut.getSessionId()).thenReturn(Optional.of("saveShouldCacheResult"));

    when(targetService.save(session)).thenThrow(DocumentConflictException.class);

    assertThrows(DocumentConflictException.class, () -> questionnaireSessionSaveService.save(session));

    var inOrder = Mockito.inOrder(cache, targetService, session);

    inOrder.verify(cache, times(1)).getName();
//    inOrder.verify(cache).evictIfPresent(Optional.of("saveShouldCacheResult"));
    inOrder.verify(targetService).save(session);
    inOrder.verifyNoMoreInteractions();

//    verify(session, atLeastOnce()).getSessionId();
    verifyNoMoreInteractions(cache, targetService, session);
  }

}
