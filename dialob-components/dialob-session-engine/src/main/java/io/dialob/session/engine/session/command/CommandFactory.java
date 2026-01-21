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
package io.dialob.session.engine.session.command;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.program.model.Value;
import io.dialob.session.engine.program.model.ValueSet;
import io.dialob.session.engine.session.command.event.ProtoTypeItemsAddedEventsProvider;
import io.dialob.session.engine.session.command.event.RowItemsAddedEventsProvider;
import io.dialob.session.engine.session.command.event.RowItemsRemovedEventsProvider;
import io.dialob.session.engine.session.command.event.ValueSetUpdatedEvent;
import io.dialob.session.engine.session.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

import static io.dialob.session.engine.session.command.CommandFactory.ErrorStateMatcher.ERROR_ACTIVITY_CHANGED;
import static io.dialob.session.engine.session.command.CommandFactory.ItemStatePredicates.*;
import static io.dialob.session.engine.session.command.Triggers.*;
import static java.util.Collections.emptyList;

public final class CommandFactory {

  private static boolean isNewOrRemoved(Object itemState, Object updateState) {
    return notSame(itemState, updateState) && (itemState == null || updateState == null);
  }

  private static boolean notSame(Object itemState, Object updateState) {
    return itemState != updateState;
  }

  private static boolean notNulls(Object itemState, Object updateState) {
    return itemState != null || updateState != null;
  }

  enum ItemStatePredicates implements BiPredicate<ItemState, ItemState> {
    ALWAYS {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return true;
      }
    },
    ITEM_STATE_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState);
      }
    },
    GROUP_ITEMS_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || !itemState.items().equals(updateState.items()));
      }
    },
    ITEM_ACTIVITY_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isActive() != itemState.isActive());
      }
    },
    ROWS_CAN_BE_ADDED_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isRowsCanBeAdded() != itemState.isRowsCanBeAdded());
      }
    },
    ROWS_CAN_BE_REMOVED_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isRowCanBeRemoved() != itemState.isRowCanBeRemoved());
      }
    },
    ITEM_LABEL_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || !Objects.equals(updateState.label(), itemState.label()));
      }
    },

    ITEM_DESCRIPTION_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || !Objects.equals(updateState.description(), itemState.description()));
      }
    },

    ITEM_REQUIRED_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isRequired() != itemState.isRequired());
      }
    },
    ITEM_READ_ONLY_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isReadOnly() != itemState.isReadOnly());
      }
    },
    ITEM_STATUS_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.status() != itemState.status());
      }
    },
    ITEM_INVALIDITY_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isInvalid() != itemState.isInvalid());
      }
    },
    ITEM_INVALID_ANSWERS_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isInvalidAnswers() != itemState.isInvalidAnswers());
      }
    },
    ITEM_ANSWERED_STATE_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isAnswered() != itemState.isAnswered());
      }
    }
  }

  enum ItemStatesPredicates implements BiPredicate<ItemStates, ItemStates> {
    ITEM_STATES_CHANGED {
      @Override
      public boolean test(ItemStates itemState, ItemStates updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || itemState.itemStates() != updateState.itemStates());
      }
    }
  }

  enum ErrorStateMatcher implements BiPredicate<ErrorState, ErrorState> {
    ERROR_ACTIVITY_CHANGED {
      @Override
      public boolean test(ErrorState itemState, ErrorState updateState) {
        return notNulls(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isActive() != itemState.isActive());
      }
    }
  }

  enum ValueStatePredicates implements BiPredicate<ValueSetState, ValueSetState> {
    VALUE_SET_STATE_CHANGED {
      @Override
      public boolean test(ValueSetState state, ValueSetState updateState) {
        return notSame(state, updateState);
      }
    }
  }

  private static final List<Trigger<ItemState>> ACTIVE_PAGE_TRIGGERS = List.of(Triggers.<ItemState>trigger(Triggers.activePageUpdatedEvent()).when(ITEM_STATE_CHANGED));

  private static final ItemUpdateCommand NEXT_PAGE = new NextPage(ACTIVE_PAGE_TRIGGERS);

  private static final ItemUpdateCommand PREV_PAGE = new PrevPage(ACTIVE_PAGE_TRIGGERS);

  private static final ItemUpdateCommand COMPLETE = new Complete(emptyList());

  private CommandFactory() {
  }

  public static ItemUpdateCommand nextPage() {
    return NEXT_PAGE;
  }

  public static ItemUpdateCommand prevPage() {
    return PREV_PAGE;
  }

  public static ItemUpdateCommand complete() {
    return COMPLETE;
  }

  public static ItemUpdateCommand gotoPage(@NonNull ItemId page) {
    return new GotoPage(page, ACTIVE_PAGE_TRIGGERS);
  }

  public static ItemUpdateCommand setAnswer(@NonNull ItemId questionId, Object answer) {
    return new SetAnswer(questionId, answer, Arrays.asList(
      Triggers.<ItemState>trigger(stateChangedEvent(questionId)).when(ITEM_STATE_CHANGED),
      Triggers.<ItemState>trigger(Triggers.validityUpdatedEvent(onTarget(questionId))).when(ITEM_INVALIDITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.answeredUpdatedEvent(onTarget(questionId))).when(ITEM_ANSWERED_STATE_CHANGED)
    ));
  }

  public static ItemUpdateCommand setLocale(@NonNull String locale) {
    return new SetLocale(locale, Collections.singletonList(
      Triggers.<ItemState>trigger(sessionLocaleUpdatedEvent()).when(ALWAYS)
    ));
  }

  public static ItemUpdateCommand setVariableValue(@NonNull ItemId id, Object value) {
    return new SetVariableValue(id, value, Arrays.asList(
      Triggers.<ItemState>trigger(stateChangedEvent(id)).when(ITEM_STATE_CHANGED),
      Triggers.<ItemState>trigger(Triggers.statusUpdatedEvent(onTarget(id))).when(ITEM_STATUS_CHANGED)
    ));
  }

  public static ItemUpdateCommand setVariablePending(@NonNull ItemId id) {
    return new SetVariablePending(id, emptyList());
  }

  public static ItemUpdateCommand setVariableFailed(@NonNull ItemId id) {
    return new SetVariableFailed(id, List.of(
      Triggers.<ItemState>trigger(Triggers.statusUpdatedEvent(onTarget(id))).when(ITEM_STATUS_CHANGED)
    ));
  }

  public static ItemUpdateCommand deleteRow(@NonNull ItemId toBeRemoved) {
    return toBeRemoved.getParent().map(parent -> (ItemUpdateCommand) new DeleteRow(parent, toBeRemoved, List.of(Triggers.<ItemState>trigger(stateChangedEvent(parent)).when(ITEM_STATE_CHANGED)))).orElseGet(() -> nop(toBeRemoved));
  }

  public static ItemUpdateCommand nop(@NonNull ItemId targetId) {
    return new NopCommand(targetId, emptyList());
  }

  public static ItemUpdateCommand addRow(@NonNull ItemId targetId) {
    return new AddRow(targetId, List.of(Triggers.<ItemState>trigger(stateChangedEvent(targetId)).when(ITEM_STATE_CHANGED)));
  }

  public static ItemUpdateCommand initRowGroupItemsCommand(@NonNull ItemId targetId) {
    return new InitRowGroupItemsCommand(targetId, List.of(Triggers.<ItemState>trigger(itemsChangedEvent(onTarget(targetId))).when(GROUP_ITEMS_CHANGED)));
  }

  public static ItemUpdateCommand activityUpdate(ItemId targetId, Expression expression) {
    return new UpdateActivityCommand(targetId, expression, List.of(
      Triggers.<ItemState>trigger(stateChangedEvent(targetId)).when(ITEM_ACTIVITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.activityUpdatedEvent(onTarget(targetId))).when(ITEM_ACTIVITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.validityUpdatedEvent(onTarget(targetId))).when(ITEM_ACTIVITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.answeredUpdatedEvent(onTarget(targetId))).when(ITEM_ACTIVITY_CHANGED)
    ));
  }

  public static ItemUpdateCommand rowsCanBeAddedUpdate(ItemId targetId, Expression expression) {
    return new UpdateRowsCanBeAddedCommand(targetId, expression, List.of(
      Triggers.<ItemState>trigger(rowsCanBeAddedUpdatedEvent(onTarget(targetId))).when(ROWS_CAN_BE_ADDED_CHANGED)
    ));
  }

  public static ItemUpdateCommand rowCanBeRemovedUpdate(ItemId targetId, Expression expression) {
    return new UpdateRowCanBeRemovedCommand(targetId, expression, List.of(
      Triggers.<ItemState>trigger(rowCanBeRemovedUpdatedEvent(onTarget(targetId))).when(ROWS_CAN_BE_REMOVED_CHANGED)
    ));
  }

  public static ItemUpdateCommand requiredUpdate(ItemId targetId, Expression expression) {
    return new UpdateRequiredCommand(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.requiredUpdatedEvent(onTarget(targetId))).when(ITEM_REQUIRED_CHANGED)));
  }

  public static ItemUpdateCommand readOnlyUpdate(ItemId targetId, Expression expression) {
    return new UpdateReadOnlyCommand(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.readOnlyUpdatedEvent(onTarget(targetId))).when(ITEM_READ_ONLY_CHANGED)));
  }

  public static ItemUpdateCommand updateClassNames(ItemId targetId, Expression expression) {
    return new UpdateClassNames(targetId, expression, emptyList());
  }

  public static ItemUpdateCommand labelUpdate(ItemId targetId, Expression expression) {
    return new UpdateLabelCommand(targetId, expression, List.of(
      Triggers.<ItemState>trigger(Triggers.labelUpdatedEvent(onTarget(targetId))).when(ITEM_LABEL_CHANGED)
    ));
  }

  public static ItemUpdateCommand descriptionUpdate(ItemId targetId, Expression expression) {
    return new UpdateDescriptionCommand(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.descriptionUpdatedEvent(onTarget(targetId))).when(ITEM_DESCRIPTION_CHANGED)));
  }

  public static ItemUpdateCommand allowedActionsUpdate(ItemId targetId, Expression expression) {
    return new UpdateAllowedActionsCommand(targetId, expression, emptyList());
  }

  public static ItemUpdateCommand updateIsInvalidAnswers(ItemId targetId, Expression expression) {
    return new UpdateIsInvalidAnswersCommand(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.anyInvalidAnswersUpdatedEvent())
      .when(ITEM_INVALID_ANSWERS_CHANGED)));
  }

  public static ItemUpdateCommand availableItemsUpdate(ItemId targetId, Expression expression) {
    return new UpdateAvailableItemsCommand(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.availableItemsUpdatedEvent())
      .when(ITEM_STATE_CHANGED)));
  }

  public static ItemUpdateCommand updateDisabled(ItemId targetId, Expression expression) {
    return new UpdateDisabledCommand(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.disabledUpdatedEvent(onTarget(targetId)))
      .when(ITEM_STATE_CHANGED)));
  }

  public static ItemUpdateCommand updateGroupItems(ItemId targetId, Expression expression) {
    if (targetId instanceof ItemIdPartial) {
      return new InitGroupItems(targetId, expression, List.of(
        // TODO Triggered event do not match correctly on command
        Triggers.trigger(Triggers.groupItemsUpdatedEvent(targetId)).when(ITEM_STATE_CHANGED),
        Triggers.trigger(Triggers.rowGroupItemsInitEvent(targetId)).when(ITEM_STATE_CHANGED)
      ));
    }
    return new UpdateGroupItems(targetId, expression, List.of(
      Triggers.trigger(Triggers.groupItemsUpdatedEvent(onTarget(targetId))).when(ITEM_STATE_CHANGED)
    ));
  }

  public static ErrorUpdateCommand validationDisabledUpdate(ErrorId errorId, Expression expression) {
    return new ValidationDisabledUpdateCommand(errorId, expression, emptyList());
  }

  public static ErrorUpdateCommand updateValidationCommand(ErrorId errorId, Expression expression) {
    return new UpdateValidationCommand(errorId, expression, List.of(
      Triggers.<ErrorState>trigger(Triggers.validityUpdatedEvent(onTarget(errorId.itemId()))).when(ERROR_ACTIVITY_CHANGED),
      Triggers.<ErrorState>trigger(Triggers.errorActivityUpdatedEvent(errorId)).when(ERROR_ACTIVITY_CHANGED)
    ));
  }

  public static ErrorUpdateCommand errorLabelUpdateCommand(ErrorId errorId, Expression expression) {
    return new ErrorLabelUpdateCommand(errorId, expression, Collections.emptyList());
  }

  public static ItemUpdateCommand variableUpdateCommand(ItemId targetId, Expression expression) {
    return new VariableUpdateCommand(targetId, expression, List.of(
      Triggers.<ItemState>trigger(stateChangedEvent(targetId)).when(ITEM_STATE_CHANGED)
    ));
  }

  public static ValueSetUpdateCommand updateValueSet(ValueSetId valueSetId, List<Value<ValueSet.Entry>> entries) {
    return new UpdateValueSetCommand(valueSetId, entries, List.of(
      Triggers.<ValueSetState>trigger(new ValueSetUpdatedEvent(valueSetId))
        .when(ValueStatePredicates.VALUE_SET_STATE_CHANGED)
      ));
  }

  public static SessionUpdateCommand createRowGroupFromPrototypeCommand(ItemId rowProtoTypeId) {
    return new CreateRowGroupFromPrototypeCommand(rowProtoTypeId,
      List.of(
        trigger(new RowItemsAddedEventsProvider(rowProtoTypeId))
          .when(ItemStatesPredicates.ITEM_STATES_CHANGED),
        trigger(new RowItemsRemovedEventsProvider(rowProtoTypeId))
          .when(ItemStatesPredicates.ITEM_STATES_CHANGED)
      )
    );
  }

  public static SessionUpdateCommand createRowGroupItemsFromPrototypeCommand(ItemId rowProtoTypeId, List<ItemId> itemPrototypeIds) {
    return new CreateRowGroupItemsFromPrototypeCommand(rowProtoTypeId, rowProtoTypeId, List.of(
      trigger(new ProtoTypeItemsAddedEventsProvider(itemPrototypeIds)).when(ItemStatesPredicates.ITEM_STATES_CHANGED)
    ));
  }
}
