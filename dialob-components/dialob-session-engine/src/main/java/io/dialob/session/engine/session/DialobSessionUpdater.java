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
package io.dialob.session.engine.session;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.proto.Action;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.Command;

import java.util.function.Consumer;

public interface DialobSessionUpdater {

  DialobSessionUpdater NOOP_UPDATER = commands -> updatedItemsVisitor -> {};

  /**
   * @deprecated Action handling should be done outside of session updater. Implementation just maps actions to commands
   *             and calls {@link #applyCommands}
   *
   * @param actions iterable of applied actions
   * @return
   */
  @Deprecated
  default Consumer<EvalContext.UpdatedItemsVisitor> dispatchActions(@NonNull Iterable<Action> actions) {
    return applyCommands(ActionToCommandMapper.toCommands(actions));
  }

  Consumer<EvalContext.UpdatedItemsVisitor> applyCommands(@NonNull Iterable<Command<?>> commands);

}
