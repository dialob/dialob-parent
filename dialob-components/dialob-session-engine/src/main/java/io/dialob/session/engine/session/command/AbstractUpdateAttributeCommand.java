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
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemState;

import java.util.HashSet;
import java.util.Set;

public interface AbstractUpdateAttributeCommand<T> extends AbstractUpdateCommand<ItemId, ItemState>, ItemUpdateCommand {

  Expression expression();

  @NonNull
  @Override
  default Set<EventMatcher> eventMatchers() {
    Set<EventMatcher> eventMatchers = expression().getEvalRequiredConditions();
    if (targetId().isPartial()) {
      var set = new HashSet<>(eventMatchers);
      set.add(EventMatchers.whenItemAdded(targetId()));
      return Set.copyOf(set);
    }
    return eventMatchers;
  }

  default T evalExpression(EvalContext context) {
    return (T) expression().eval(context);
  }

}
