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
import io.dialob.session.engine.session.command.event.Event;
import org.immutables.value.Value;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

@Value.Immutable
public interface DynamicTrigger<T> extends Trigger<T> {

  @Value.Parameter
  @Override
  BiPredicate<T,T> getWhen();

  @Value.Parameter
  Triggers.EventsProvider<T> getEventsProvider();

  /**
   * Return trigger action when trigger condition matches
   *
   * @param originalState item's original state
   * @param updateState item's updated state
   * @return triggered event, when item state change matches
   */
  @NonNull
  @Override
  default Stream<Event> apply(@NonNull T originalState, T updateState) {
    return getWhen().test(originalState, updateState) ? getEventsProvider().createEvents(originalState, updateState) : Stream.empty();
  }

  default List<Event> getAllEvents() {
    return getEventsProvider().createEvents(null, null).toList();
  }

}
