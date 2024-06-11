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
package io.dialob.questionnaire.service;

import io.dialob.api.proto.Action;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ImmutableActions;
import io.dialob.common.Constants;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionSaveService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.NonNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
public class QuestionnaireSessionProcessingService implements ActionProcessingService {

  private final QuestionnaireSessionService questionnaireSessionService;

  private final Timer processingTime;

  private final Counter numberOfFailures;

  private final Timer updateTime;

  private Optional<CacheManager> sessionCacheManager;

  private final QuestionnaireSessionSaveService questionnaireSessionSaveService;

  private final QuestionnaireEventPublisher eventPublisher;


  public QuestionnaireSessionProcessingService(
    QuestionnaireSessionService questionnaireSessionService,
    MeterRegistry meterRegistry,
    Optional<CacheManager> sessionCacheManager,
    QuestionnaireSessionSaveService questionnaireSessionSaveService,
    QuestionnaireEventPublisher eventPublisher) {
    this.questionnaireSessionService = questionnaireSessionService;

    this.numberOfFailures = Counter
      .builder("dialob.session.actions.failures")
      .description("Number of failed actions")
      .register(meterRegistry);

    this.processingTime = Timer
      .builder("dialob.session.actions.processingTime")
      .description("Actions processing time")
      .register(meterRegistry);

    this.updateTime = Timer
      .builder("dialob.session.update.time")
      .description("Actions processing time")
      .register(meterRegistry);


    this.sessionCacheManager = sessionCacheManager;
    this.questionnaireSessionSaveService = questionnaireSessionSaveService;
    this.eventPublisher = eventPublisher;
  }


  @NonNull
  @Override
  @Deprecated
  public Actions answerQuestion(@NonNull final String questionnaireId, String revision, @NonNull final List<Action> actions) {
    return this.processingTime.record(() -> {
      try {
        int retries = 5;
        Actions returnActions = null;
        do {
          try {
            returnActions = runUpdate(questionnaireId, revision, actions);
            break;
          } catch (DocumentConflictException e) {
            LOGGER.warn("Update conflict on {}.. retry update {}", questionnaireId, retries);
            if (--retries <= 0) {
              throw e;
            }
          } catch (Exception e) {
            throw e;
          }
        } while (retries > 0);
        return returnActions;
      } catch (Exception e) {
        numberOfFailures.increment();
        LOGGER.error("Action processing failure on questionnaireId {} error: {}", questionnaireId, e.getMessage(), e);
        throw e;
      }
    });
  }

  private Actions runUpdate(String questionnaireId, String revision, List<Action> actions) {
    final QuestionnaireSession session = questionnaireSessionService.findOne(questionnaireId);
    if (session == null) {
      throw new DocumentNotFoundException(String.format("Could not find questionnaire %s", questionnaireId));
    }
    if (session.isCompleted()) {
      return ImmutableActions.builder().rev(session.getRev()).build();
    }
    final QuestionnaireSession.DispatchActionsResult response = session.dispatchActions(revision, actions);
    if (response.isDidComplete()) {
      // when completed Note! save method updates also cache
      questionnaireSessionSaveService.save(session);
      session.getSessionId().ifPresent(sessionId -> eventPublisher.completed(session.getTenantId(), sessionId));
      return response.getActions();
    }
    // normal
    this.storeSessionIntoCache(questionnaireId, session);
    return response.getActions();
  }

  @Nonnull
  @Override
  public QuestionnaireSession computeSessionUpdate(@Nonnull String questionnaireId, boolean openIfClosed, Function<QuestionnaireSession, QuestionnaireSession> updateFunction) {
    return this.updateTime.record(() -> {
      try {
        final QuestionnaireSession session = questionnaireSessionService.findOne(questionnaireId);
        if (session == null) {
          return null;
        }
        final QuestionnaireSession updatedSession = updateFunction.apply(session);
        if (updatedSession != session) {
          this.storeSessionIntoCache(questionnaireId, updatedSession);
        }
        return updatedSession;
      } catch (Exception e) {
        LOGGER.error("Processing failure on questionnaireId : {}", questionnaireId, e);
        throw e;
      }
    });
  }

  public void storeSessionIntoCache(@NonNull final String questionnaireId, @NonNull final QuestionnaireSession session) {
    sessionCacheManager.ifPresent(cacheManager -> {
      Cache cache = cacheManager.getCache(Constants.SESSION_CACHE_NAME);
      if (cache != null) {
        cache.put(questionnaireId, session);
      }
    });
  }

}
