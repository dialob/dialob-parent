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
import io.dialob.session.engine.session.model.ErrorId;
import io.dialob.session.engine.session.model.ErrorState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

record ErrorLabelUpdateCommand(
  ErrorId targetId,
  Expression expression,
  List<Trigger<ErrorState>> triggers
) implements ErrorUpdateCommand {

  @NonNull
  @Override
  public UpdateCommand<ErrorId, ErrorState> withTargetId(@NonNull ErrorId targetId) {
    return new ErrorLabelUpdateCommand(targetId, expression(), triggers);
  }

  @NonNull
  @Override
  public ErrorState update(@NonNull EvalContext context, @NonNull ErrorState errorState) {
    // label update will not trigger additional expressions
    return errorState.update(context)
      .setLabel((String) expression().eval(context)).get();
  }

  @NonNull
  @Override
  public Set<EventMatcher> eventMatchers() {
    var set = new HashSet<>(expression().getEvalRequiredConditions());
    set.add(EventMatchers.whenSessionLocaleUpdated());
    if (targetId().isPartial()) {
      set.add(EventMatchers.whenItemAdded(targetId().itemId()));
    }
    return Set.copyOf(set);
  }

}
