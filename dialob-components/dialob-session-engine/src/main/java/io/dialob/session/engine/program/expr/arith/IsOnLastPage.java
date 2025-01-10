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
package io.dialob.session.engine.program.expr.arith;

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.command.EventMatcher;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.ItemId;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;

import static io.dialob.session.engine.session.command.EventMatchers.whenActivePageUpdated;
import static io.dialob.session.engine.session.command.EventMatchers.whenAvailableItemsUpdated;

@Value.Immutable
public interface IsOnLastPage extends Expression {

  @Override
  default Boolean eval(@NonNull EvalContext context) {
    return context.getItemState(DialobSession.QUESTIONNAIRE_REF).map(questionnaire -> questionnaire.getActivePage().map(activePage -> {
      List<ItemId> availableItems = questionnaire.getAvailableItems();
      if (availableItems.size() <= 1) {
        return true;
      }
      int i = availableItems.indexOf(activePage);
      return i == availableItems.size() - 1;
    }).orElse(false)).orElse(false);
  }

  @NonNull
  @Override
  default ValueType getValueType() {
    return ValueType.BOOLEAN;
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEvalRequiredConditions() {
    return ImmutableSet.of(whenActivePageUpdated(), whenAvailableItemsUpdated());
  }
}
