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

import io.dialob.session.engine.session.command.event.Event;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;

record StaticTrigger<T>(
  BiPredicate<T, T> when,
  List<Event> allEvents
) implements Trigger<T> {

  public StaticTrigger {
    when = Objects.requireNonNull(when, "when may not be null");
    allEvents = List.copyOf(Objects.requireNonNull(allEvents, "allEvents may not be null"));
  }

}
