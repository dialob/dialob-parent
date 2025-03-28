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
package io.dialob.session.engine.program.expr.arith;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.ImmutableAsyncFunctionCall;
import io.dialob.session.engine.session.command.EventMatcher;
import org.apache.commons.lang3.mutable.MutableObject;
import org.immutables.value.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Value.Immutable
public interface FunctionCallOperator extends Expression {

  @NonNull
  ValueType getValueType();

  String getFunctionName();

  List<Expression> getArgs();

  @Override
  default Object eval(@NonNull EvalContext evalContext) {
    final FunctionRegistry functionRegistry = evalContext.getFunctionRegistry();
    final Object[] args = getArgs().stream().map(arg -> arg.eval(evalContext)).toArray();
    if (functionRegistry.isAsyncFunction(getFunctionName())) {
      return ImmutableAsyncFunctionCall.builder()
        .functionName(getFunctionName())
        .args(args)
        .build();
    } else {
      MutableObject<Object> holder = new MutableObject<>();
      functionRegistry.invokeFunction(new FunctionRegistry.FunctionCallback() {
                                        @Override
                                        public void succeeded(Object result) {
                                          holder.setValue(result);
                                        }

                                        @Override
                                        public void failed(@NonNull String error) {
                                          // TODO Add error handling
                                        }
                                      },
        getFunctionName(),
        args);
      return holder.getValue();
    }
  }

  @NonNull
  @Override
  default Set<EventMatcher> getEvalRequiredConditions() {
    final var deps = new HashSet<EventMatcher>();
    getArgs().forEach(arg -> deps.addAll(arg.getEvalRequiredConditions()));
    return Set.copyOf(deps);
  }

}
