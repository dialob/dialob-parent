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
package io.dialob.session.engine;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.model.DialobSession;

import java.util.function.Consumer;

public interface DialobSessionUpdateHook {

  /**
   * All actions to session are passed through this hook. Implementation can filter, replace or add updates to be
   * processed.
   *
   * @param dialobSession current DialobSession
   * @param update update to check
   * @param delegate delegates update for actual evaluation
   */
  void hookAction(@NonNull DialobSession dialobSession,
                  @NonNull Command<?> update,
                  Consumer<Command<?>> delegate);
}
