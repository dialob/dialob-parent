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
package io.dialob.session.rest;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ImmutableAction;
import io.dialob.api.proto.ImmutableActions;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.questionnaire.service.api.ActionProcessingService;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.questionnaire.service.api.FormActionsUpdatesCallback;
import io.dialob.questionnaire.service.api.QuestionnaireActionsService;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.security.user.CurrentUser;
import io.dialob.security.user.CurrentUserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DefaultAnswerController implements AnswerController, QuestionnaireActionsService {

  private final QuestionnaireSessionService questionnaireSessionService;
  private final boolean returnStackTrace;
  private final ActionProcessingService actionProcessingService;
  private final SessionPermissionEvaluator sessionPermissionEvaluator;
  private final Optional<CurrentUserProvider> currentUserProvider;
  private long warningThreshold = 2000000000L; // 2 seconds

  DefaultAnswerController(
    QuestionnaireSessionService questionnaireSessionService,
    ActionProcessingService actionProcessingService,
    SessionPermissionEvaluator sessionPermissionEvaluator,
    boolean returnStackTrace,
    Optional<CurrentUserProvider> currentUserProvider)
  {
    this.questionnaireSessionService = questionnaireSessionService;
    this.actionProcessingService = actionProcessingService;
    this.sessionPermissionEvaluator = sessionPermissionEvaluator;
    this.returnStackTrace = returnStackTrace;
    this.currentUserProvider = currentUserProvider;
  }

  public void setWarningThreshold(long warningThreshold) {
    this.warningThreshold = warningThreshold;
  }

  @Override
  public ResponseEntity<Actions> getState(String sessionId) {
    long start = System.nanoTime();
    LOGGER.debug("Received 'GET /{}' request", sessionId);

    if (!sessionPermissionEvaluator.hasAccess(sessionId, currentUser())) {
      return createQuestionnaireNotFoundResponse(sessionId, null);
    }
    final ImmutableActions.Builder actions = ImmutableActions.builder();
    try {
      QuestionnaireSession questionnaireSession = getQuestionnaireSession(sessionId);
      FormActions formActions = new FormActions();
      questionnaireSession.buildFullForm(new FormActionsUpdatesCallback(formActions));
      actions.actions(formActions.getActions());
      actions.rev(questionnaireSession.getRevision());
    } catch(DocumentNotFoundException e) {
      return createQuestionnaireNotFoundResponse(sessionId, e);
    } catch(Exception e) {
      LOGGER.error(String.format("Dialog fetch failed: %s", e.getMessage()), e);
      return createServiceErrorResponse(e);
    }
    long time = System.nanoTime() - start;
    if (time > warningThreshold) {
      LOGGER.warn("Request time {}ns exceeds warning threshold {}.", time, warningThreshold);
    } else if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("Request time {}ms", time / 1e6);
    }
    return ResponseEntity.ok(actions.build());
  }

  private String currentUser() {
    if (currentUserProvider.isPresent()) {
      CurrentUser currentUser = currentUserProvider.get().get();
      if (currentUser!=null) {
        return currentUser.getUserId();
      }
    } else {
      LOGGER.debug("No currentUserProvider defined");
    }
    return null;
  }

  @Override
  public ResponseEntity<Actions> answers(@NonNull String sessionId, Actions actions) {
    if (!sessionPermissionEvaluator.hasAccess(sessionId, currentUser())) {
      return createQuestionnaireNotFoundResponse(sessionId, null);
    }
    long start = System.nanoTime();
    try {
      return ResponseEntity.ok(answerQuestion(sessionId, actions.getRev(), actions.getActions()));
    } catch(DocumentNotFoundException e) {
      return createQuestionnaireNotFoundResponse(sessionId, e);
    } catch(DocumentConflictException e) {
      return createUpdateConflictResponse(sessionId, e);
    } catch(Exception e) {
      LOGGER.error("Dialog {} update failed: {}", sessionId, e.getMessage(), e);
      return createServiceErrorResponse(e);
    } finally {
      long time = System.nanoTime() - start;
      if (time > warningThreshold) {
        LOGGER.warn("Request time {}ns exceeds warning threshold {}.", time, warningThreshold);
      } else if (LOGGER.isTraceEnabled()) {
        LOGGER.trace("Request time {}ms", time / 1e6);
      }
    }
  }

  @NonNull
  protected QuestionnaireSession getQuestionnaireSession(String sessionId) {
    final QuestionnaireSession questionnaireSession = questionnaireSessionService.findOne(sessionId);
    if (questionnaireSession == null) {
      throw new DocumentNotFoundException("Questionnaire " + sessionId + " not found");
    }
    return questionnaireSession;
  }

  protected ResponseEntity<Actions> createQuestionnaireNotFoundResponse(String sessionId, @Nullable DocumentNotFoundException e) {
    LOGGER.debug("Action QUESTIONNAIRE_NOT_FOUND: backend response '{}'", e != null ? e.getMessage() : "Security block");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      ImmutableActions.builder().addActions(ImmutableAction.builder()
        .type(Action.Type.SERVER_ERROR)
        .serverEvent(true)
        .message("not found")
        .id(sessionId).build()).build());
  }

  protected ResponseEntity<Actions> createUpdateConflictResponse(String sessionId, @NonNull DocumentConflictException e) {
    LOGGER.debug("Action UPDATE_CONFLICT: backend response '{}'", e.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      ImmutableActions.builder().addActions(ImmutableAction.builder()
        .type(Action.Type.SERVER_ERROR)
        .serverEvent(true)
        .message(e.getMessage())
        .id(sessionId).build()).build());
  }

  private ResponseEntity<Actions> createServiceErrorResponse(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ImmutableActions.builder().addActions(createNotifyServerErrorAction(e)).build());
  }

  private Action createNotifyServerErrorAction(Exception e) {
    final ImmutableAction.Builder action = ImmutableAction.builder();
    action.type(Action.Type.SERVER_ERROR);
    action.serverEvent(true);
    if (returnStackTrace) {
      StringWriter sw = new StringWriter();
      e.printStackTrace(new PrintWriter(sw));
      action.message(e.getMessage());
      action.trace(sw.toString());
    } else {
      if (e instanceof DocumentConflictException) {
        action.message(e.getMessage());
      }
    }
    return action.build();
  }

  @NonNull
  @Override
  public Actions answerQuestion(@NonNull String questionnaireId, String revision, @NonNull List<Action> actions) {
    return actionProcessingService.answerQuestion(questionnaireId, revision, actions);
  }
}
