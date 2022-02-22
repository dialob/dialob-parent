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
package io.dialob.session.engine.program.expr.arith;

import com.google.common.collect.ImmutableSet;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.Utils;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.model.ErrorState;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.dialob.session.engine.session.command.EventMatchers.anyError;
import static io.dialob.session.engine.session.command.EventMatchers.errorActivity;

@Value.Immutable
public interface IsInvalidAnswersOnActivePage extends Expression {

  Set<EventMatcher> ANY_ERROR = ImmutableSet.of(errorActivity(anyError()));

  ItemId getPageContainerId();

  @Override
  default Boolean eval(@Nonnull EvalContext context) {

    final Set<ItemId> questionsWithErrors = context.getErrorStates().stream()
      .filter(ErrorState::isActive)
      .map(ErrorState::getItemId)
      .collect(Collectors.toSet());
    if (questionsWithErrors.isEmpty()) {
      return false;
    }

    final List<ItemId> pageItemIds = context.getItemState(getPageContainerId())
      .flatMap(ItemState::getActivePage)
      .flatMap(context::getItemState)
      .map(ItemState::getItems)
      .orElse(Collections.emptyList());

    return findQuestionItems(context, pageItemIds)
      .map(ItemState::getId)
      .anyMatch(questionsWithErrors::contains);
  }

  default Stream<? extends ItemState> findQuestionItems(@Nonnull EvalContext context, List<ItemId> items) {
    return items.stream()
      .map(context::getItemState)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .flatMap(item -> {
        if (Utils.isQuestionType(item)) {
          return Stream.of(item);
        }
        return findQuestionItems(context, item.getItems());
      });
  }

  @Nonnull
  @Override
  default ValueType getValueType() {
    return ValueType.BOOLEAN;
  }

  @Nonnull
  @Override
  default Set<EventMatcher> getEvalRequiredConditions() {
    return ANY_ERROR;
  }
}
