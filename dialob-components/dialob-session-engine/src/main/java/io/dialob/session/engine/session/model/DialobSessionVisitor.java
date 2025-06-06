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

import java.util.Optional;

public interface DialobSessionVisitor {

  @FunctionalInterface
  interface ItemVisitor {
    void visitItemState(@NonNull ItemState itemState);
    default void end() {}
  }

  @FunctionalInterface
  interface ValueSetVisitor {
    void visitValueSetState(@NonNull ValueSetState valueSetState);
    default void end() {}
  }

  @FunctionalInterface
  interface ErrorVisitor {
    void visitErrorState(@NonNull ErrorState errorState);
    default void end() {}
  }

  default void start() {}

  default Optional<ItemVisitor> visitItemStates() {
    return Optional.empty();
  }

  default Optional<ValueSetVisitor> visitValueSetStates() {
    return Optional.empty();
  }

  default Optional<ErrorVisitor> visitErrorStates() {
    return Optional.empty();
  }

  default void end() {}

}
