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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.security.tenant.ImmutableTenant;
import io.dialob.security.tenant.TenantContextHolderCurrentTenant;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class LocalQuestionnaireSessionCache implements QuestionnaireSessionCache {

  private final ConcurrentMap<String, QuestionnaireSession> sessionCache = new ConcurrentHashMap<>();

  private final String name;

  public LocalQuestionnaireSessionCache(String name) {
    this.name = name;
  }

  @Override
  public int size() {
    return sessionCache.size();
  }

  protected ValueWrapper internalGet(@NonNull String questionnaireId) {
    QuestionnaireSession session = sessionCache.get(questionnaireId);
    if (session == null) {
      return null;
    }
    return () -> session;
  }

  public synchronized void evict(@NonNull String sessionId, Function<QuestionnaireSession,QuestionnaireSession> beforeCloseCallback) {
    final QuestionnaireSession questionnaireSession = sessionCache.get(sessionId);
    if (questionnaireSession == null) {
      return;
    }
    if (beforeCloseCallback == null) {
      LOGGER.warn("Evicting session \"{}\" without callback.", sessionId);
    } else {
      LOGGER.info("Evicting session \"{}\".", sessionId);
    }

    TenantContextHolderCurrentTenant.runInTenantContext(ImmutableTenant.builder()
      .id(questionnaireSession.getTenantId())
      .name(Optional.empty()).build(), () -> {
      String rev = questionnaireSession.getRev();
      String revAfterCallback = rev;
      QuestionnaireSession questionnaireSessionToEvict = questionnaireSession;
      if (questionnaireSessionToEvict.isActive()) {
        questionnaireSessionToEvict.passivate();
        if (beforeCloseCallback != null) {
          // Note! Callback may call QuestionnaireSessionService.save and update session in cache
          try {
            questionnaireSessionToEvict = beforeCloseCallback.apply(questionnaireSessionToEvict);
          } catch (DocumentConflictException dc) {
            LOGGER.warn("Conflict on session {} persist. Evicting session anyway.", sessionId);
          } catch (Exception e) {
            LOGGER.error("Eviction callback failed: {}", e.getMessage());
          }
          revAfterCallback = questionnaireSessionToEvict.getRev();
        }
      }
      LOGGER.debug("Closing session {}, rev = {}, revAfterCallback = {}", sessionId, rev, revAfterCallback);
      questionnaireSessionToEvict.close();
      if(!sessionCache.remove(sessionId, questionnaireSessionToEvict)) {
        LOGGER.warn("Could not evict session {}. New session appeared. rev = {}, revAfterCallback = {}", sessionId, rev, revAfterCallback);
      }
    });
  }

  @NonNull
  protected QuestionnaireSession put(@NonNull QuestionnaireSession questionnaireSession) {
    return questionnaireSession.getSessionId().map(sessionId -> {
      LOGGER.debug("Caching session {} rev {}", sessionId, questionnaireSession.getRev());
      QuestionnaireSession previousSession = sessionCache.put(sessionId, questionnaireSession);
      if (previousSession == null) {
        LOGGER.debug("New session {} added to cache", sessionId);
        questionnaireSession.activate();
      }
      return questionnaireSession;
    }).orElseGet(() -> {
      LOGGER.warn("Cannot cache session without id.");
      return questionnaireSession;
    });
  }

  @Override
  public void forEach(@NonNull Consumer<QuestionnaireSession> sessionConsumer) {
    sessionCache.values().forEach(sessionConsumer);
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  @NonNull
  @Override
  public Object getNativeCache() {
    return this.sessionCache;
  }

  @Override
  public ValueWrapper get(@NonNull Object key) {
    return internalGet((String) key);
  }

  @Override
  public <T> T get(@NonNull Object key, Class<T> type) {
    final ValueWrapper valueWrapper = internalGet((String) key);
    if (valueWrapper == null) {
      return null;
    }
    return (T) valueWrapper.get();
  }

  @Override
  public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
    final ValueWrapper valueWrapper = internalGet((String) key);
    if (valueWrapper == null) {
      try {
        return valueLoader.call();
      } catch (Exception e) {
        throw new ValueRetrievalException(key, valueLoader, e);
      }
    }
    return (T) valueWrapper.get();
  }

  @Override
  public void put(@NonNull Object key, Object value) {
    putIfAbsent(key, value);
  }

  @Override
  public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
    if (!(key instanceof String || key instanceof Optional)) {
      throw new IllegalArgumentException("questionnaireSession cache key must be String or Optional");
    }
    if (!(value instanceof QuestionnaireSession)) {
      throw new IllegalArgumentException("questionnaireSession cache value must be type of QuestionnaireSession");
    }
    QuestionnaireSession session = put((QuestionnaireSession) value);
    return () -> session;
  }

  @Override
  public void evict(@NonNull Object key) {
    evict((String) key, null);
  }

  @Override
  public void clear() {
    String[] ids = sessionCache.keySet().toArray(new String[0]);
    for (String id : ids) {
      evict(id);
    }
  }
}
