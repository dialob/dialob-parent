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
package io.dialob.session.engine.sp;

import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.security.tenant.CurrentTenant;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DialobQuestionnaireSessionSaveServiceTest {

  @Test
  void shouldSaveQuestionnaireSession() {
    QuestionnaireDatabase questionnaireDatabase = mock();
    CurrentTenant currentTenant = mock();
    DialobQuestionnaireSessionSaveService service = new DialobQuestionnaireSessionSaveService(questionnaireDatabase, currentTenant);

    QuestionnaireSession session = mock();
    Questionnaire questionnaire = mock();
    Questionnaire savedQuestionnaire = mock();
    QuestionnaireSession updatedSession = mock();

    when(currentTenant.getId()).thenReturn("tenant1");
    when(session.getQuestionnaire()).thenReturn(questionnaire);
    when(questionnaireDatabase.save(eq("tenant1"), any(Questionnaire.class))).thenReturn(savedQuestionnaire);
    when(savedQuestionnaire.getId()).thenReturn("id1");
    when(savedQuestionnaire.getRev()).thenReturn("rev1");
    when(session.withIdAndRev("id1", "rev1")).thenReturn(updatedSession);

    QuestionnaireSession result = service.save(session);

    assertEquals(updatedSession, result);
    verify(questionnaireDatabase).save("tenant1", questionnaire);
    verify(session).withIdAndRev("id1", "rev1");
  }
}
