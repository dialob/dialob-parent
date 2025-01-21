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

/**
 * Service responsible for managing periodic persistence of questionnaire sessions
 * in response to specific questionnaire-related events. Utilizes a thread pool task scheduler
 * to schedule and manage save operations for active sessions.
 * <p>
 * This service listens for {@link QuestionnaireActionsEvent}, and upon detection,
 * schedules a task that saves the associated questionnaire session after a configured delay.
 * If a previously scheduled task for the same questionnaire ID is still active, it is canceled
 * before scheduling a new one.
 * <p>
 * Periodic persistence tasks are only executed for sessions that are not completed.
 * The save operation is delegated to {@link QuestionnaireSessionSaveService}, which also
 * leverages caching for returned session data.
 * <p>
 * Key components:
 * - The {@link QuestionnaireSessionService} is utilized to retrieve session details.
 * - The {@link ThreadPoolTaskScheduler} is used to schedule and execute save operations.
 * - The {@link DialobSettings} is used to configure the time interval for the autosave tasks.
 * - The {@link CurrentTenant} enables tenant-aware operations to ensure session persistence aligns with the current tenant context.
 * <p>
 * Logging is performed to provide insight into service activation, session save events,
 * and any relevant debug details.
 */
@Slf4j
public class PeriodicPersistenceService {

  private final QuestionnaireSessionService questionnaireSessionService;

  private final QuestionnaireSessionSaveService questionnaireSessionSaveService;

  private final ConcurrentHashMap<String, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

  private final ThreadPoolTaskScheduler scheduler;

  private final long interval;

  PeriodicPersistenceService(@NonNull QuestionnaireSessionService questionnaireSessionService,
                             @NonNull QuestionnaireSessionSaveService questionnaireSessionSaveService,
                             @NonNull ThreadPoolTaskScheduler taskScheduler,
                             long interval) {
    this.questionnaireSessionService = questionnaireSessionService;
    this.questionnaireSessionSaveService = questionnaireSessionSaveService;
    this.scheduler = taskScheduler;
    this.interval = interval;
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
      }, interval, TimeUnit.MILLISECONDS);
    });
  }
}
