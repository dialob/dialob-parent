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
package io.dialob.session.engine.session.command;

import com.google.common.collect.Sets;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Action;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.model.ItemId;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.Set;

@Value.Immutable
public interface UpdateAllowedActionsCommand extends AbstractUpdateCommand<ItemId, ItemState>, ItemUpdateCommand {

  @Value.Parameter(order = 1)
  Expression getExpression();

  @NonNull
  @Override
  default Set<EventMatcher> getEventMatchers() {
    return getExpression().getEvalRequiredConditions();
  }

  @NonNull
  @Override
  default ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    return itemState.update()
      .setAllowedActions(evalExpression(context)).get();
  }

  default Set<Action.Type> evalExpression(EvalContext context) {
    return Sets.newHashSet((Collection<Action.Type>) getExpression().eval(context));
  }

}
