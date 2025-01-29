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

import com.google.common.collect.Sets;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ErrorState;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
public interface ErrorLabelUpdateCommand extends ErrorUpdateCommand {

  @NonNull
  @Override
  default ErrorState update(@NonNull EvalContext context, @NonNull ErrorState errorState) {
    // label update will not trigger additional expressions
    return errorState.update(context)
      .setLabel((String) getExpression().eval(context)).get();
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEventMatchers() {
    Set<EventMatcher> eventMatchers = Sets.union(Set.of(EventMatchers.whenSessionLocaleUpdated()), getExpression().getEvalRequiredConditions());
    if (getTargetId().isPartial()) {
      return Sets.union(eventMatchers, Set.of(EventMatchers.whenItemAdded(getTargetId().getItemId())));
    }
    return eventMatchers;
  }



}
