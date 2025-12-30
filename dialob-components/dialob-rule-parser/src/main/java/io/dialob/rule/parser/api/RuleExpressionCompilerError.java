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
package io.dialob.rule.parser.api;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.node.Span;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record RuleExpressionCompilerError(

  @NonNull
  String errorCode,

  @NonNull
  List<Object> args,

  @NonNull
  Span span

) implements Serializable {

  public static RuleExpressionCompilerError of(@NonNull String errorCode, Span span, Object... args) {
    return new RuleExpressionCompilerError(errorCode, Arrays.asList(args), span);
  }

  public RuleExpressionCompilerError {
    errorCode = Objects.requireNonNull(errorCode, "errorCode may not be null");
    args = Objects.requireNonNullElseGet(args, List::of);
    span = Objects.requireNonNullElseGet(span, Span::undefined);
  }

}
