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

import com.google.common.collect.ImmutableSet;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.ItemState;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
public interface UpdateDescriptionCommand extends AbstractUpdateAttributeCommand<String> {

  @NonNull
  @Override
  default ItemState update(@NonNull EvalContext context, @NonNull ItemState itemState) {
    // description update will not trigger additional expressions
    return itemState.update()
      .setDescription(evalExpression(context)).get();
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEventMatchers() {
    Set<EventMatcher> parent = AbstractUpdateAttributeCommand.super.getEventMatchers();
    return ImmutableSet.<EventMatcher>builder().addAll(parent).add(EventMatchers.whenSessionLocaleUpdated()).build();
  }

}
