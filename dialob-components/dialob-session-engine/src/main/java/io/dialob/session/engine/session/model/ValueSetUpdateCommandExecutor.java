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
package io.dialob.session.engine.session.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.ValueSetUpdateCommand;

import java.util.Map;

public class ValueSetUpdateCommandExecutor extends AbstractCommandExecutor<ValueSetId, ValueSetState, ValueSetUpdateCommand> {

  @Override
  protected Map<ValueSetId, ValueSetState> mutableItems(@NonNull EvalContext context) {
    return context.mutableItemStates().valueSetStates();
  }

  protected void registerUpdate(EvalContext context, ValueSetUpdateCommand command, ValueSetState state, ValueSetState updatedState) {
    context.registerUpdate(updatedState, state);
  }



}
