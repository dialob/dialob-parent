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
package io.dialob.session.engine.program;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.DialobSessionUpdateHook;
import io.dialob.session.engine.session.ActiveDialobSessionUpdater;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.model.DialobSession;

import java.util.function.Consumer;

public class DialobSessionEvalContextFactory {

  @NonNull
  private final FunctionRegistry functionRegistry;

  @Nullable
  private final DialobSessionUpdateHook dialobSessionUpdateHook;

  public DialobSessionEvalContextFactory(@NonNull FunctionRegistry functionRegistry, @Nullable DialobSessionUpdateHook dialobSessionUpdateHook) {
    this.functionRegistry = functionRegistry;
    this.dialobSessionUpdateHook = dialobSessionUpdateHook;
  }

  @NonNull
  public DialobSessionUpdater createSessionUpdater(@NonNull DialobProgram dialobProgram, @NonNull DialobSession dialobSession, boolean activating) {
    if (dialobSession.isCompleted()) {
      return DialobSessionUpdater.NOOP_UPDATER;
    }
    return new ActiveDialobSessionUpdater((@NonNull Consumer<Event> updatesConsumer) -> new DialobSessionEvalContext(functionRegistry, dialobSession, updatesConsumer, activating, dialobSessionUpdateHook), dialobProgram);
  }

}
