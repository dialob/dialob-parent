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
import io.dialob.session.engine.program.model.Expression;
import org.immutables.value.Value;

import java.util.Collection;

@Value.Builder
@Value.Style(
  jakarta = true,
  jdkOnly = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
public record SizeOperator(
  Expression expression
) implements UnaryOperator {

  public static final class Builder extends SizeOperatorBuilder {}

  @Override
  public Object apply(@NonNull Object value) {
    if (value.getClass().isArray()) {
      return ((Object[]) value).length;
    }
    if (value instanceof Collection<?> collection) {
      return collection.size();
    }
    if (value instanceof String string) {
      return string.length();
    }
    return null;
  }

  @NonNull
  @Override
  public ValueType getValueType() {
    return ValueType.INTEGER;
  }

}
