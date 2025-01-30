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
import io.dialob.session.engine.session.command.event.ImmutableProtoTypeItemsAddedEventsProvider;
import io.dialob.session.engine.session.command.event.ImmutableRowItemsAddedEventsProvider;
import io.dialob.session.engine.session.command.event.ImmutableRowItemsRemovedEventsProvider;
import io.dialob.session.engine.session.command.event.ImmutableValueSetUpdatedEvent;
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

  private static boolean isNew(Object itemState, Object updateState) {
    return itemState == null && updateState != null;
  }

  private static boolean isNewOrRemoved(Object itemState, Object updateState) {
    return itemState != updateState && (itemState == null || updateState == null);
  }

  private static boolean notSame(Object itemState, Object updateState) {
    return itemState != updateState;
  }

  private static boolean notRemoved(Object itemState, Object updateState) {
    return itemState == null || updateState != null;
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
        return notSame(itemState, updateState) && (isNew(itemState, updateState) || updateState != itemState) && notRemoved(itemState, updateState);
      }
    },
    GROUP_ITEMS_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || !itemState.getItems().equals(updateState.getItems()));
      }
    },
    ITEM_ACTIVITY_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isActive() != itemState.isActive());
      }
    },
    ROWS_CAN_BE_ADDED_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isRowsCanBeAdded() != itemState.isRowsCanBeAdded());
      }
    },
    ROWS_CAN_BE_REMOVED_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isRowCanBeRemoved() != itemState.isRowCanBeRemoved());
      }
    },
    ITEM_LABEL_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || !Objects.equals(updateState.getLabel(), itemState.getLabel()));
      }
    },

    ITEM_DESCRIPTION_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || !Objects.equals(updateState.getDescription(), itemState.getDescription()));
      }
    },

    ITEM_REQUIRED_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isRequired() != itemState.isRequired());
      }
    },
    ITEM_STATUS_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.getStatus() != itemState.getStatus());
      }
    },
    ITEM_INVALIDITY_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isInvalid() != itemState.isInvalid());
      }
    },
    ITEM_INVALID_ANSWERS_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isInvalidAnswers() != itemState.isInvalidAnswers());
      }
    },
    ITEM_ANSWERED_STATE_CHANGED {
      @Override
      public boolean test(ItemState itemState, ItemState updateState) {
        return notSame(itemState, updateState) && (isNewOrRemoved(itemState, updateState) || updateState.isAnswered() != itemState.isAnswered());
      }
    }
  }

  enum ItemStatesPredicates implements BiPredicate<ItemStates, ItemStates> {
    ITEM_STATES_CHANGED {
      @Override
      public boolean test(ItemStates itemState, ItemStates itemState2) {
        return itemState.getItemStates() != itemState2.getItemStates();
      }
    }
  }

  enum ErrorStateMatcher implements BiPredicate<ErrorState, ErrorState> {
    ERROR_ACTIVITY_CHANGED {
      @Override
      public boolean test(ErrorState itemState, ErrorState updateState) {
        return isNewOrRemoved(itemState, updateState) || updateState.isActive() != itemState.isActive();
      }
    }
  }

  enum ValueStatePredicates implements BiPredicate<ValueSetState, ValueSetState> {
    VALUE_SET_STATE_CHANGED {
      @Override
      public boolean test(ValueSetState state, ValueSetState updateState) {
        return notSame(state, updateState) && (isNew(state, updateState) || state != updateState) && notRemoved(state, updateState);
      }
    }
  }

  private static final List<Trigger<ItemState>> ACTIVE_PAGE_TRIGGERS = List.of(Triggers.<ItemState>trigger(Triggers.activePageUpdatedEvent()).when(ITEM_STATE_CHANGED));

  private static final NextPage NEXT_PAGE = ImmutableNextPage.of(ACTIVE_PAGE_TRIGGERS);

  private static final PrevPage PREV_PAGE = ImmutablePrevPage.of(ACTIVE_PAGE_TRIGGERS);

  private static final Complete COMPLETE = ImmutableComplete.builder().build();

  private CommandFactory() {
  }

  public static NextPage nextPage() {
    return NEXT_PAGE;
  }

  public static PrevPage prevPage() {
    return PREV_PAGE;
  }

  public static Complete complete() {
    return COMPLETE;
  }

  public static GotoPage gotoPage(@NonNull ItemId page) {
    return ImmutableGotoPage.of(page, ACTIVE_PAGE_TRIGGERS);
  }

  public static SetAnswer setAnswer(@NonNull ItemId questionId, Object answer) {
    return ImmutableSetAnswer.of(questionId, answer, Arrays.asList(
      Triggers.<ItemState>trigger(stateChangedEvent(questionId)).when(ITEM_STATE_CHANGED),
      Triggers.<ItemState>trigger(Triggers.validityUpdatedEvent(onTarget(questionId))).when(ITEM_INVALIDITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.answeredUpdatedEvent(onTarget(questionId))).when(ITEM_ANSWERED_STATE_CHANGED)
    ));
  }

  public static SetLocale setLocale(@NonNull String locale) {
    return ImmutableSetLocale.of(locale, Collections.singletonList(
      Triggers.<ItemState>trigger(sessionLocaleUpdatedEvent()).when(ALWAYS)
    ));
  }

  public static SetVariableValue setVariableValue(@NonNull ItemId id, Object value) {
    return ImmutableSetVariableValue.of(id, value, Arrays.asList(
      Triggers.<ItemState>trigger(stateChangedEvent(id)).when(ITEM_STATE_CHANGED),
      Triggers.<ItemState>trigger(Triggers.statusUpdatedEvent(onTarget(id))).when(ITEM_STATUS_CHANGED)
    ));
  }

  public static SetVariableFailed setVariableFailed(@NonNull ItemId id) {
    return ImmutableSetVariableFailed.of(id, List.of(
      Triggers.<ItemState>trigger(Triggers.statusUpdatedEvent(onTarget(id))).when(ITEM_STATUS_CHANGED)
    ));
  }

  public static ItemUpdateCommand deleteRow(@NonNull ItemId toBeRemoved) {
    return toBeRemoved.getParent().map(parent -> (ItemUpdateCommand) ImmutableDeleteRow.of(parent, toBeRemoved, List.of(Triggers.<ItemState>trigger(stateChangedEvent(parent)).when(ITEM_STATE_CHANGED)))).orElseGet(() -> ImmutableNopCommand.of(toBeRemoved, emptyList()));
  }

  public static AddRow addRow(@NonNull ItemId targetId) {
    return ImmutableAddRow.of(targetId, List.of(Triggers.<ItemState>trigger(stateChangedEvent(targetId)).when(ITEM_STATE_CHANGED)));
  }

  public static InitRowGroupItemsCommand initRowGroupItemsCommand(@NonNull ItemId targetId) {
    return ImmutableInitRowGroupItemsCommand.of(targetId, List.of(Triggers.<ItemState>trigger(itemsChangedEvent(onTarget(targetId))).when(GROUP_ITEMS_CHANGED)));
  }

  public static UpdateActivityCommand activityUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateActivityCommand.of(targetId, expression, List.of(
      Triggers.<ItemState>trigger(stateChangedEvent(targetId)).when(ITEM_ACTIVITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.activityUpdatedEvent(onTarget(targetId))).when(ITEM_ACTIVITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.validityUpdatedEvent(onTarget(targetId))).when(ITEM_ACTIVITY_CHANGED),
      Triggers.<ItemState>trigger(Triggers.answeredUpdatedEvent(onTarget(targetId))).when(ITEM_ACTIVITY_CHANGED)
    ));
  }

  public static UpdateRowsCanBeAddedCommand rowsCanBeAddedUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateRowsCanBeAddedCommand.of(targetId, expression, List.of(
      Triggers.<ItemState>trigger(rowsCanBeAddedUpdatedEvent(onTarget(targetId))).when(ROWS_CAN_BE_ADDED_CHANGED)
    ));
  }

  public static UpdateRowCanBeRemovedCommand rowCanBeRemovedUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateRowCanBeRemovedCommand.of(targetId, expression, List.of(
      Triggers.<ItemState>trigger(rowCanBeRemovedUpdatedEvent(onTarget(targetId))).when(ROWS_CAN_BE_REMOVED_CHANGED)
    ));
  }

  public static UpdateRequiredCommand requiredUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateRequiredCommand.of(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.requiredUpdatedEvent(onTarget(targetId))).when(ITEM_REQUIRED_CHANGED)));
  }

  public static UpdateClassNames updateClassNames(ItemId targetId, Expression expression) {
    return ImmutableUpdateClassNames.of(targetId, expression, emptyList());
  }

  public static UpdateLabelCommand labelUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateLabelCommand.of(targetId, expression, List.of(
      Triggers.<ItemState>trigger(Triggers.labelUpdatedEvent(onTarget(targetId))).when(ITEM_LABEL_CHANGED)
    ));
  }

  public static UpdateDescriptionCommand descriptionUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateDescriptionCommand.of(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.descriptionUpdatedEvent(onTarget(targetId))).when(ITEM_DESCRIPTION_CHANGED)));
  }

  public static UpdateAllowedActionsCommand allowedActionsUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateAllowedActionsCommand.of(targetId, expression, emptyList());
  }

  public static UpdateIsInvalidAnswersCommand updateIsInvalidAnswers(ItemId targetId, Expression expression) {
    return ImmutableUpdateIsInvalidAnswersCommand.of(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.anyInvalidAnswersUpdatedEvent())
      .when(ITEM_INVALID_ANSWERS_CHANGED)));
  }

  public static UpdateAvailableItemsCommand availableItemsUpdate(ItemId targetId, Expression expression) {
    return ImmutableUpdateAvailableItemsCommand.of(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.availableItemsUpdatedEvent())
      .when(ITEM_STATE_CHANGED)));
  }

  public static UpdateDisabledCommand updateDisabled(ItemId targetId, Expression expression) {
    return ImmutableUpdateDisabledCommand.of(targetId, expression, List.of(Triggers.<ItemState>trigger(Triggers.disabledUpdatedEvent(onTarget(targetId)))
      .when(ITEM_STATE_CHANGED)));
  }

  public static ItemUpdateCommand updateGroupItems(ItemId targetId, Expression expression) {
    if (targetId instanceof ItemIdPartial) {
      return ImmutableInitGroupItems.of(targetId, expression, List.of(
        // TODO Triggered event do not match correctly on command
        Triggers.trigger(Triggers.groupItemsUpdatedEvent(targetId)).when(ITEM_STATE_CHANGED),
        Triggers.trigger(Triggers.rowGroupItemsInitEvent(targetId)).when(ITEM_STATE_CHANGED)
      ));
    }
    return ImmutableUpdateGroupItems.of(targetId, expression, List.of(
      Triggers.trigger(Triggers.groupItemsUpdatedEvent(onTarget(targetId))).when(ITEM_STATE_CHANGED)
    ));
  }

  public static ValidationDisabledUpdateCommand validationDisabledUpdate(ErrorId errorId, Expression expression) {
    return ImmutableValidationDisabledUpdateCommand.of(errorId, expression, emptyList());
  }

  public static UpdateValidationCommand updateValidationCommand(ErrorId errorId, Expression expression) {
    return ImmutableUpdateValidationCommand.of(errorId, expression, List.of(
      Triggers.<ErrorState>trigger(Triggers.validityUpdatedEvent(onTarget(errorId.getItemId()))).when(ERROR_ACTIVITY_CHANGED),
      Triggers.<ErrorState>trigger(Triggers.errorActivityUpdatedEvent(errorId)).when(ERROR_ACTIVITY_CHANGED)
    ));
  }

  public static ErrorLabelUpdateCommand errorLabelUpdateCommand(ErrorId errorId, Expression expression) {
    return ImmutableErrorLabelUpdateCommand.of(errorId, expression, Collections.emptyList());
  }

  public static VariableUpdateCommand variableUpdateCommand(ItemId targetId, Expression expression) {
    return ImmutableVariableUpdateCommand.of(targetId, expression, List.of(
      Triggers.<ItemState>trigger(stateChangedEvent(targetId)).when(ITEM_STATE_CHANGED)
    ));
  }

  public static UpdateValueSetCommand updateValueSet(ValueSetId valueSetId, List<Value<ValueSet.Entry>> entries) {
    return ImmutableUpdateValueSetCommand.of(valueSetId, entries, List.of(
      Triggers.<ValueSetState>trigger(ImmutableValueSetUpdatedEvent.of(valueSetId))
        .when(ValueStatePredicates.VALUE_SET_STATE_CHANGED)
      ));
  }

  public static SessionUpdateCommand createRowGroupFromPrototypeCommand(ItemId rowProtoTypeId) {
    return ImmutableCreateRowGroupFromPrototypeCommand.of(rowProtoTypeId,
      List.of(
        trigger(ImmutableRowItemsAddedEventsProvider.of(rowProtoTypeId))
          .when(ItemStatesPredicates.ITEM_STATES_CHANGED),
        trigger(ImmutableRowItemsRemovedEventsProvider.of(rowProtoTypeId))
          .when(ItemStatesPredicates.ITEM_STATES_CHANGED)
      )
    );
  }

  public static SessionUpdateCommand createRowGroupItemsFromPrototypeCommand(ItemId rowProtoTypeId, List<ItemId> itemPrototypeIds) {
    return ImmutableCreateRowGroupItemsFromPrototypeCommand.of(rowProtoTypeId, rowProtoTypeId, List.of(
      trigger(ImmutableProtoTypeItemsAddedEventsProvider.of(itemPrototypeIds)).when(ItemStatesPredicates.ITEM_STATES_CHANGED)
    ));
  }
}
