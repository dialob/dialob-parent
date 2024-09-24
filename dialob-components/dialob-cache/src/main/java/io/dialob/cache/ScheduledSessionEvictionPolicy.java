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
import io.dialob.integration.api.event.FormUpdatedEvent;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
public class ScheduledSessionEvictionPolicy {

  private final Clock clock;

  private final QuestionnaireSessionCache cache;

  private final Function<QuestionnaireSession,QuestionnaireSession> sessionEvictionCallback;

  private final Optional<CacheManager> cacheManager;

  private final Integer ttl;

  public ScheduledSessionEvictionPolicy(Clock clock,
                                        QuestionnaireSessionCache cache,
                                        Function<QuestionnaireSession,QuestionnaireSession> sessionEvictionCallback,
                                        Optional<CacheManager> cacheManager,
                                        Integer ttl) {
    this.clock = clock;
    this.cache = cache;
    this.sessionEvictionCallback = Objects.requireNonNullElseGet(sessionEvictionCallback, Function::identity);
    this.cacheManager = cacheManager;
    this.ttl = ttl != null ? ttl: 60000;
  }

  @Scheduled(fixedRateString = "${dialob.session.cache.evict-rate:2000}")
  public void evictQuietSessions() {
    LOGGER.debug("evictQuietSessions");
    final Instant now = clock.instant();
    evictWhen(session -> session.getStatus() == Questionnaire.Metadata.Status.COMPLETED || Duration.between(session.getLastUpdate(), now).toMillis() > ttl);
  }

  protected void evictWhen(Predicate<QuestionnaireSession> evictCondition) {
    final Set<String> sessionsToEvict = new HashSet<>();
    cache.forEach(session -> {
      if (evictCondition.test(session)) {
        session.getSessionId().ifPresent(sessionsToEvict::add);
      }
    });
    sessionsToEvict.forEach(this::evict);
  }


  protected void evict(String sessionId) {
    cache.evict(sessionId, sessionEvictionCallback);
  }

  @EventListener
  protected void onFormUpdatedEvent(FormUpdatedEvent event) {
    LOGGER.debug("onFormUpdatedEvent({})", event);
    final String formId = event.getFormId();
    evictWhen(session -> session.usesLastestFormRevision() && formId.equals(session.getFormId()));
    cacheManager.flatMap(cacheManager -> Optional.ofNullable(cacheManager.getCache(Constants.PROGRAM_CACHE_NAME)))
      .ifPresent(programCache -> programCache.evict(formId));
  }


}
