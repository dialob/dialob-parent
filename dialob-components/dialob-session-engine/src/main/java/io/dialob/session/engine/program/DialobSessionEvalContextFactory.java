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
package io.dialob.session.engine.program;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.DialobSessionUpdateHook;
import io.dialob.session.engine.session.ActiveDialobSessionUpdater;
import io.dialob.session.engine.session.DialobSessionUpdater;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.model.DialobSession;

import java.util.function.Consumer;

public class DialobSessionEvalContextFactory {

  private final FunctionRegistry functionRegistry;


  private final DialobSessionUpdateHook dialobSessionUpdateHook;

  public DialobSessionEvalContextFactory(FunctionRegistry functionRegistry, DialobSessionUpdateHook dialobSessionUpdateHook) {
    this.functionRegistry = functionRegistry;
    this.dialobSessionUpdateHook = dialobSessionUpdateHook;
  }

  @NonNull
  public DialobSessionEvalContext createDialobSessionEvalContext(@NonNull DialobSession dialobSession,
                                                                 @NonNull Consumer<Event> updatesConsumer,
                                                                 boolean activating) {
    return new DialobSessionEvalContext(functionRegistry, dialobSession, updatesConsumer, activating, dialobSessionUpdateHook);
  }


  public DialobSessionUpdater createSessionUpdater(@NonNull DialobProgram dialobProgram, @NonNull DialobSession dialobSession) {
    if (dialobSession.isCompleted()) {
      return DialobSessionUpdater.NOOP_UPDATER;
    }
    return new ActiveDialobSessionUpdater(this, dialobProgram, dialobSession);
  }

}
