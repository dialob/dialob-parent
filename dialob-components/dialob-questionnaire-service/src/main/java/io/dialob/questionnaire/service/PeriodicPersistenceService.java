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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.event.QuestionnaireActionsEvent;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.settings.DialobSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PeriodicPersistenceService {

  private final QuestionnaireSessionService questionnaireSessionService;

  private final QuestionnaireSessionSaveService questionnaireSessionSaveService;

  private final ConcurrentHashMap<String, ScheduledFuture> tasks = new ConcurrentHashMap<>();

  private final ThreadPoolTaskScheduler scheduler;

  private final DialobSettings settings;

  private final CurrentTenant currentTenant;

  PeriodicPersistenceService(@NonNull QuestionnaireSessionService questionnaireSessionService,
                             @NonNull QuestionnaireSessionSaveService questionnaireSessionSaveService,
                             @NonNull ThreadPoolTaskScheduler taskScheduler,
                             @NonNull DialobSettings settings,
                             @NonNull CurrentTenant currentTenant) {
    this.questionnaireSessionService = questionnaireSessionService;
    this.questionnaireSessionSaveService = questionnaireSessionSaveService;
    this.scheduler = taskScheduler;
    this.settings = settings;
    this.currentTenant = currentTenant;
    LOGGER.info("Periodic Persistence Service: activated");
  }

  @EventListener
  public void onQuestionnaireActionsEvent(QuestionnaireActionsEvent event) {
    tasks.compute(event.getQuestionnaireId(), (qId, previousSchedule) -> {
      if (previousSchedule != null && !previousSchedule.isDone()) {
        previousSchedule.cancel(false);
      }
      return scheduler.getScheduledThreadPoolExecutor().schedule(() -> {
        final QuestionnaireSession session = questionnaireSessionService.findOne(qId, false);
        if (session != null && session.getStatus() != Questionnaire.Metadata.Status.COMPLETED) {
          // save method's return value will be cached
          questionnaireSessionSaveService.save(session);
          LOGGER.debug("Periodic Persistence Service: session '{}' stored", qId);
        }
        tasks.remove(qId);
      }, settings.getSession().getAutosave().getInterval(), TimeUnit.MILLISECONDS);
    });
  }
}
