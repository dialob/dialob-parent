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
package io.dialob.session.engine.sp;

import com.google.common.collect.Streams;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.proto.*;
import io.dialob.api.questionnaire.Error;
import io.dialob.api.questionnaire.*;
import io.dialob.common.Constants;
import io.dialob.questionnaire.service.api.FormActions;
import io.dialob.questionnaire.service.api.FormActionsUpdatesCallback;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.ImmutableQuestionnaireSession;
import io.dialob.questionnaire.service.api.session.QuestionnaireSession;
import io.dialob.questionnaire.service.api.utils.ConversionUtil;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.FormActionsUpdatesItemsVisitor;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.DisplayItem;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.model.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.dialob.session.engine.Utils.*;

@Slf4j
@EqualsAndHashCode(exclude = {"eventPublisher", "state"})
@ToString
public class DialobQuestionnaireSession implements QuestionnaireSession {

  enum State {
    NEW,
    ACTIVATING,
    ACTIVE,
    PASSIVATING,
    PASSIVE
  }


  private final String rev;

  private final DialobSession dialobSession;

  private final DialobProgram dialobProgram;

  private final QuestionClientVisibility questionClientVisibility;

  private final AtomicReference<State> state;

  private final Questionnaire.Metadata metadata;

  private final transient QuestionnaireEventPublisher eventPublisher;

  private final transient DialobSessionEvalContextFactory sessionContextFactory;

  private final transient AsyncFunctionInvoker asyncFunctionInvoker;

  private final transient Function<ItemState, ActionItem> toActionItemFunction;

  private DialobQuestionnaireSession(String rev, @NonNull DialobSession dialobSession, @NonNull DialobQuestionnaireSession dialobQuestionnaireSession) {
    this(
      dialobQuestionnaireSession.eventPublisher,
      dialobQuestionnaireSession.sessionContextFactory,
      dialobQuestionnaireSession.asyncFunctionInvoker,
      dialobSession,
      dialobQuestionnaireSession.dialobProgram,
      rev,
      dialobQuestionnaireSession.metadata,
      dialobQuestionnaireSession.state.get(),
      dialobQuestionnaireSession.questionClientVisibility
    );
  }

  private DialobQuestionnaireSession(@NonNull QuestionnaireEventPublisher eventPublisher,
                                     @NonNull DialobSessionEvalContextFactory sessionContextFactory,
                                     @NonNull AsyncFunctionInvoker asyncFunctionInvoker,
                                     @NonNull DialobSession dialobSession,
                                     @NonNull DialobProgram dialobProgram,
                                     String rev,
                                     @NonNull Questionnaire.Metadata metadata,
                                     @NonNull State state,
                                     @NonNull QuestionClientVisibility questionClientVisibility) {
    this.rev = rev;
    this.metadata = metadata;
    this.eventPublisher = eventPublisher;
    this.sessionContextFactory = sessionContextFactory;
    this.asyncFunctionInvoker = asyncFunctionInvoker;
    this.dialobSession = Objects.requireNonNull(dialobSession, "dialobSession may not be null");
    this.dialobProgram = dialobProgram;
    this.questionClientVisibility = questionClientVisibility;
    this.state = new AtomicReference<>(state);
    this.toActionItemFunction = itemState -> Utils.toActionItem(itemState, builder -> {
      if (itemState.hasCustomProps()) {
        ItemId id = itemState.getPrototypeId();
        if (id == null) {
          id = itemState.getId();
        }
        dialobProgram.getItem(id)
          .filter(item -> item instanceof DisplayItem)
          .map(item -> ((DisplayItem) item).getProps())
          .ifPresent(builder::props);
      }
      return builder;
    });
  }

  public QuestionnaireSession withIdAndRev(String id, String rev) {
    return new DialobQuestionnaireSession(rev, dialobSession.withId(id), this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public void writeTo(@NonNull CodedOutputStream output) throws IOException {
    writeNullableString(output, rev);
    output.writeInt32NoTag(questionClientVisibility.ordinal());
    output.writeInt32NoTag(state.get().ordinal());

    output.writeStringNoTag(metadata.getStatus().name());
    output.writeStringNoTag(metadata.getFormId());
    writeNullableString(output, metadata.getFormRev());
    writeNullableString(output, metadata.getLanguage());
    writeNullableString(output, metadata.getLabel());
    writeNullableDate(output, metadata.getCreated());
    writeNullableDate(output, metadata.getLastAnswer());
    writeNullableString(output, metadata.getCreator());
    writeNullableString(output, metadata.getOwner());
    writeNullableString(output, metadata.getTenantId());
    writeNullableString(output, metadata.getSubmitUrl());

    output.writeInt32NoTag(metadata.getAdditionalProperties().size());
    for (Map.Entry<String, Object> entry : metadata.getAdditionalProperties().entrySet()) {
      output.writeStringNoTag(entry.getKey());
      Utils.writeObjectValue(output, entry.getValue());
    }
    dialobSession.writeTo(output);
  }

  public static class Builder {

    private QuestionnaireEventPublisher eventPublisher;

    private DialobSessionEvalContextFactory sessionContextFactory;

    private AsyncFunctionInvoker asyncFunctionInvoker;

    private String rev;

    private DialobSession dialobSession;

    private DialobProgram dialobProgram;

    private State state = State.NEW;

    private QuestionClientVisibility questionClientVisibility = QuestionClientVisibility.ONLY_ENABLED;

    @Getter
    private Questionnaire.Metadata metadata;

    public Builder readFrom(@NonNull CodedInputStream input) throws IOException {
      rev = readNullableString(input);
      questionClientVisibility = QuestionClientVisibility.values()[input.readInt32()];
      state = State.values()[input.readInt32()];

      ImmutableQuestionnaireMetadata.Builder metadataBuilder = ImmutableQuestionnaireMetadata.builder()
        .status(Questionnaire.Metadata.Status.valueOf(input.readString()))
        .formId(input.readString())
        .formRev(readNullableString(input))
        .language(readNullableString(input))
        .label(readNullableString(input))
        .created(readNullableDate(input))
        .lastAnswer(readNullableDate(input))
        .creator(readNullableString(input))
        .owner(readNullableString(input))
        .tenantId(readNullableString(input))
        .submitUrl(readNullableString(input));

      int additionalPropertiesCount = input.readInt32();
      for (int i = 0; i < additionalPropertiesCount; ++i) {
        String key = input.readString();
        Object value = Utils.readObjectValue(input);
        metadataBuilder.putAdditionalProperties(key, value);
      }

      metadata = metadataBuilder.build();
      dialobSession = DialobSession.readFrom(input);
      return this;
    }

    public Builder eventPublisher(QuestionnaireEventPublisher eventPublisher) {
      this.eventPublisher = eventPublisher;
      return this;
    }

    public Builder sessionContextFactory(DialobSessionEvalContextFactory sessionContextFactory) {
      this.sessionContextFactory = sessionContextFactory;
      return this;
    }

    public Builder asyncFunctionInvoker(AsyncFunctionInvoker asyncFunctionInvoker) {
      this.asyncFunctionInvoker = asyncFunctionInvoker;
      return this;
    }

    public Builder rev(String rev) {
      this.rev = rev;
      return this;
    }

    public Builder dialobSession(DialobSession dialobSession) {
      this.dialobSession = dialobSession;
      return this;
    }

    public Builder dialobProgram(DialobProgram dialobProgram) {
      this.dialobProgram = dialobProgram;
      return this;
    }

    public Builder state(State state) {
      this.state = state;
      return this;
    }

    public Builder questionClientVisibility(@NonNull QuestionClientVisibility questionClientVisibility) {
      this.questionClientVisibility = questionClientVisibility;
      return this;
    }

    public Builder metadata(Questionnaire.Metadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public DialobQuestionnaireSession build() {
      return new DialobQuestionnaireSession(
        Objects.requireNonNull(eventPublisher, "eventPublisher is null"),
        Objects.requireNonNull(sessionContextFactory, "sessionContextFactory is null"),
        Objects.requireNonNull(asyncFunctionInvoker, "asyncFunctionInvoker is null"),
        dialobSession,
        dialobProgram,
        rev,
        metadata,
        state,
        questionClientVisibility);
    }
  }


  @NonNull
  @Override
  public DispatchActionsResult dispatchActions(String revision, @NonNull Collection<Action> actions) {
    var actionsResultBuilder = ImmutableQuestionnaireSession
      .DispatchActionsResult.builder()
      .isDidComplete(false);
    var prevRevision = dialobSession.getRevision();
    if (isCompleted()) {
      return actionsResultBuilder
        .actions(ImmutableActions.builder()
          .rev(prevRevision)
          .build())
        .build();
    }
    final FormActions formActions = new FormActions();
    try {
      MDC.put(Constants.QUESTIONNAIRE, getSessionId().orElse("new-session"));
      boolean revisionMatch = revision != null && revision.equals(prevRevision);
      LOGGER.debug("revision comparison: {} vs. {} == {}", revision,prevRevision, revisionMatch);
      sessionContextFactory.createSessionUpdater(dialobProgram, dialobSession)
        .dispatchActions(actions, state.get() == State.ACTIVATING)
        .accept(new EvalContext.AbstractDelegateUpdatedItemsVisitor(new FormActionsUpdatesItemsVisitor(formActions, getIsVisiblePredicate(), this.toActionItemFunction)) {
          @Override
          public void visitCompleted() {
            super.visitCompleted();
            actionsResultBuilder.isDidComplete(true);
          }

          @Override
          public Optional<AsyncFunctionCallVisitor> visitAsyncFunctionCalls() {
            return getSessionId().map(asyncFunctionInvoker::createVisitor);
          }
        });
      // broadcast user actions to other nodes, but do not return user actions back to original client
      List<Action> broadcastActions;
      if (!revisionMatch) {
        // reset updates and build form from ground up
        formActions.clear();
        buildFullForm(new FormActionsUpdatesCallback(formActions));
        broadcastActions = formActions.getActions();
      } else {
        // Merge user actions with updates for a broadcast
        broadcastActions = Streams.concat(
          actions
            .stream()
            .filter(action -> action.getType().isClientAction()),
          formActions
            .getActions()
            .stream()
        ).toList();
      }
      String newRevision = dialobSession.getRevision();
      if (!broadcastActions.isEmpty() && isActive()) {
        publishQuestionnaireActions(newRevision, broadcastActions);
      }
      var actionsResult = actionsResultBuilder
        .actions(ImmutableActions.builder()
          .actions(formActions.getActions())
          .rev(newRevision)
          .build())
        .build();
      if (actionsResult.isDidComplete()) {
        getSessionId().ifPresent(sessionId -> eventPublisher.completed(getDialobSession().getTenantId(), sessionId));
      }
      return actionsResult;
    } finally {
      MDC.remove(Constants.QUESTIONNAIRE);
    }
  }

  @NonNull
  @Override
  public DispatchActionsResult dispatchActions(@NonNull Collection<Action> actions) {
    return dispatchActions(dialobSession.getRevision(), actions);
  }

  @NonNull
  @Override
  public Questionnaire getQuestionnaire() {
    return ImmutableQuestionnaire.builder()
      .id(dialobSession.getId())
      .rev(this.rev)
      .context(getContextVariableValues())
      .answers(getAnswers())
      .errors(getErrors())
      .variableValues(getVariableValues())
      .activeItem(getActiveItem().orElse(null)) // deprecated
      .valueSets(getProvidedValueSets())
      .metadata(getQuestionnaireMetadata())
      .build();
  }

  @Override
  @NonNull
  public Questionnaire.Metadata getQuestionnaireMetadata() {
    Questionnaire.Metadata.Status status;
    if (dialobSession.isCompleted()) {
      status = Questionnaire.Metadata.Status.COMPLETED;
    } else {
      status = switch (state.get()) {
        case ACTIVATING, NEW -> Questionnaire.Metadata.Status.NEW;
        default -> Questionnaire.Metadata.Status.OPEN;
      };
    }
    return ImmutableQuestionnaireMetadata.builder()
      .from(metadata)
      .lastAnswer(new Date(dialobSession.getLastUpdate().toEpochMilli()))
      .completed(dialobSession.getCompleted() != null ? new Date(dialobSession.getCompleted().toEpochMilli()) : null)
      .opened(dialobSession.getOpened() != null ? new Date(dialobSession.getOpened().toEpochMilli()) : null)
      .status(status)
      .language(dialobSession.getLanguage())
      .tenantId(dialobSession.getTenantId())
      .build();
  }

  private Iterable<? extends ValueSet> getProvidedValueSets() {
    return () -> dialobSession.getValueSetStates().values().stream().map(state ->
      (ValueSet) ImmutableValueSet.builder()
        .id(state.getId().getValueSetId())
        .entries(() -> state.getEntries().stream()
          .filter(ValueSetState.Entry::isProvided)
          .map(entry ->
            (ValueSetEntry) ImmutableValueSetEntry.builder()
              .key(entry.getId())
              .value(entry.getLabel())
              .build()).iterator()
        ).build()).iterator();
  }

  private Iterable<? extends ContextValue> getContextVariableValues() {
    final List<ContextValue> answers = new ArrayList<>();
    dialobSession.accept(new DialobSessionVisitor() {
      @Override
      public Optional<ItemVisitor> visitItemStates() {
        return Optional.of(itemState -> {
          if (Utils.isContextVariable(itemState.getType())) {
            Object value = ConversionUtil.toJSON(itemState.getValue());
            answers.add(ImmutableContextValue.builder().id(IdUtils.toString(itemState.getId())).value(value == null ? null : value.toString()).build());
          }
        });
      }
    });
    return answers;
  }

  @NonNull
  @Override
  public String getRevision() {
    return dialobSession.getRevision();
  }

  @Override
  public String getId() {
    return dialobSession.getId();
  }

  @Override
  public String getRev() {
    return rev;
  }

  @Override
  @Nullable
  public String getOwner() {
    return this.metadata.getOwner();
  }

  @NonNull
  @Override
  public Instant getLastUpdate() {
    return dialobSession.getLastUpdate();
  }

  @Override
  public Optional<String> getActiveItem() {
    return dialobSession.getRootItem().getActivePage().map(IdUtils::toString);
  }

  @NonNull
  @Override
  public List<io.dialob.api.proto.ValueSet> getValueSets() {
    List<io.dialob.api.proto.ValueSet> valueSets = new ArrayList<>();
    dialobSession.accept(new DialobSessionVisitor() {
      @Override
      public Optional<ValueSetVisitor> visitValueSetStates() {
        return Optional.of(valueSetState -> valueSets.add(ImmutableValueSet.builder().id(IdUtils.toString(valueSetState.getId())).entries(
          valueSetState.getEntries().stream().map(entry -> ImmutableValueSetEntry.builder().key(entry.getId()).value(entry.getLabel()).build()).collect(Collectors.toList())
        ).build()));
      }
    });
    return valueSets;
  }

  @NonNull
  @Override
  public List<Error> getErrors() {
    List<Error> errors = new ArrayList<>();
    dialobSession.accept(new DialobSessionVisitor() {
      @Override
      public Optional<ErrorVisitor> visitErrorStates() {
        return Optional.of(errorState -> {
          if (errorState.isActive()) {
            errors.add(ImmutableError.builder()
              .code(errorState.getCode())
              .id(IdUtils.toString(errorState.getItemId()))
              .description(errorState.getLabel()).build()
            );
          }
        });
      }
    });
    return errors;
  }

  @NonNull
  @Override
  public List<ActionItem> getItems() {
    final List<ActionItem> formItems = new ArrayList<>();
    dialobSession.accept(new DialobSessionVisitor() {
      @Override
      public Optional<ItemVisitor> visitItemStates() {
        return Optional.of(itemState -> formItems.add(toActionItemFunction.apply(itemState)));
      }
    });
    return formItems;
  }

  @Override
  public Optional<ActionItem> getItemById(@NonNull String itemId) {
    return dialobSession.getItemState(IdUtils.toId(itemId)).map(toActionItemFunction);
  }

  @NonNull
  @Override
  public List<ActionItem> getVisibleItems() {
    final List<ActionItem> formItems = new ArrayList<>();
    Predicate<SessionObject> isVisiblePredicate = getIsVisiblePredicate();
    dialobSession.accept(new DialobSessionVisitor() {
      @Override
      public Optional<ItemVisitor> visitItemStates() {
        return Optional.of(itemState -> {
          if (isVisiblePredicate.test(itemState)) {
            formItems.add(toActionItemFunction.apply(itemState));
          }
        });
      }
    });
    return formItems;
  }

  @NonNull
  @Override
  public List<Answer> getAnswers() {
    final List<Answer> answers = new ArrayList<>();
    dialobSession.accept(new DialobSessionVisitor() {
      @Override
      public Optional<ItemVisitor> visitItemStates() {
        return Optional.of(itemState -> {
          if (itemState.isActive() && isQuestionType(itemState)) {
            final ImmutableAnswer.Builder answerBuilder = ImmutableAnswer.builder()
              .id(IdUtils.toString(itemState.getId()))
              .value(itemState.getAnswer());
            ItemId itemId = itemState.getPrototypeId();
            if (itemId == null) {
              itemId = itemState.getId();
            }

            dialobProgram.getItem(itemId).ifPresent(item -> {
              ValueType valueType = item.getValueType();
              if (valueType != null) {
                answerBuilder.type(valueType.getName());
              }
            });
            answers.add(answerBuilder.build());
          }
        });
      }
    });
    return answers;
  }

  @NonNull
  @Override
  public List<VariableValue> getVariableValues() {
    final List<VariableValue> answers = new ArrayList<>();
    dialobSession.accept(new DialobSessionVisitor() {
      @Override
      public Optional<ItemVisitor> visitItemStates() {
        return Optional.of(itemState -> {
          if (Utils.isProgramVariable(itemState.getType())) {
            Object value = ConversionUtil.toJSON(itemState.getValue());
            answers.add(ImmutableVariableValue.builder().id(IdUtils.toString(itemState.getId())).value(value == null ? null : value.toString()).build());
          }
        });
      }
    });
    return answers;
  }

  @Override
  public void buildFullForm(@NonNull UpdatesCallback updatesCallback) {
    updatesCallback.removeAll();
    getLocale().ifPresent(updatesCallback::locale);
    getVisibleItems().forEach(updatesCallback::questionAdded);
    getValueSets().forEach(updatesCallback::valueSetAdded);
    getErrors().forEach(updatesCallback::errorAdded);
    if (dialobSession.isCompleted()) {
      updatesCallback.completed();
    }
  }

  @Override
  public Optional<String> getSessionId() {
    return Optional.ofNullable(dialobSession.getId());
  }

  @Override
  public String getTenantId() {
    return dialobSession.getTenantId();
  }

  protected void initialize() {
    // run initialization rules
    getSessionId().ifPresent(eventPublisher::created);
  }

  public boolean activate() {
    if (!state.compareAndSet(State.PASSIVE, State.ACTIVATING) && !state.compareAndSet(State.NEW, State.ACTIVATING)) {
      return false;
    }

    List<Action> restoreActions = new ArrayList<>();
    // row group answers needs ro be restored.. but how??
    if (metadata.getStatus() == Questionnaire.Metadata.Status.COMPLETED) {
      restoreActions.add(ActionsFactory.complete(dialobSession.getId()));
    }
    // run activation rules
    dispatchActions(restoreActions);
    state.set(State.ACTIVE);
    getSessionId().ifPresent(eventPublisher::opened);
    return true;
  }

  @NonNull
  static String[] convertRows(List<String> rows) {
    Pattern pattern = Pattern.compile("^([^\\[]+)\\[(\\d+)]$");
    return rows.stream()
      .map(pattern::matcher)
      .filter(Matcher::matches)
      .map(matcher -> matcher.group(1) + "." + matcher.group(2)).toArray(String[]::new);
  }

  @Override
  public boolean passivate() {
    if (!state.compareAndSet(State.ACTIVE, State.PASSIVATING)) {
      return false;
    }
    return state.compareAndSet(State.PASSIVATING, State.PASSIVE);
  }

  @Override
  public boolean isActive() {
    return state.get() == State.ACTIVE;
  }

  @Override
  public boolean isCompleted() {
    return getStatus() == Questionnaire.Metadata.Status.COMPLETED;
  }

  @Override
  public boolean usesLastestFormRevision() {
    return true;
  }

  @NonNull
  @Override
  public String getFormId() {
    return dialobProgram.getProgram().getId();
  }

  @Override
  public Optional<Locale> getLocale() {
    final String language = dialobSession.getLanguage();
    if (StringUtils.isNotBlank(language)) {
      return Optional.of(new Locale(language));
    }
    return Optional.empty();
  }

  @Override
  public QuestionClientVisibility getQuestionClientVisibility() {
    return questionClientVisibility;
  }

  @Override
  public void close() {
    // nothing to close here
  }

  Predicate<SessionObject> getIsVisiblePredicate() {
    return switch (questionClientVisibility) {
      case ALL -> itemState -> itemState != null && itemState.isDisplayItem();
      case SHOW_DISABLED -> itemState -> itemState != null && itemState.isDisplayItem() && itemState.isActive();
      default ->
        itemState -> itemState != null && itemState.isDisplayItem() && itemState.isActive() && !itemState.isDisabled();
    };
  }

  private void publishQuestionnaireActions(String nextRevision, List<Action> actionQueue) {
    if (!actionQueue.isEmpty() && eventPublisher != null) {
      final ImmutableActions.Builder builder = ImmutableActions.builder().rev(nextRevision);
      actionQueue.stream().map(action -> ImmutableAction.builder().from(action).serverEvent(true).build()).forEach(builder::addActions);
      getSessionId().ifPresent(sessionId -> eventPublisher.actions(sessionId, builder.build()));
    }
  }

  @NonNull
  @Override
  public Questionnaire.Metadata.Status getStatus() {
    return dialobSession.isCompleted() ? Questionnaire.Metadata.Status.COMPLETED : Questionnaire.Metadata.Status.OPEN;
  }

  @NonNull
  public DialobProgram getDialobProgram() {
    return dialobProgram;
  }

  @NonNull
  public DialobSession getDialobSession() {
    return dialobSession;
  }

}
