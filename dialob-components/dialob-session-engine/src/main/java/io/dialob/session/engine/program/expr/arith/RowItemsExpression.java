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
package io.dialob.session.engine.program.expr.arith;

import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.ItemId;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Value.Immutable
public interface RowItemsExpression extends Expression {

  List<ItemId> getItemIds();

  @Nonnull
  @Override
  default ValueType getValueType() {
    return ValueType.arrayOf(ValueType.STRING);
  }

  @Override
  default Collection<ItemId> eval(@Nonnull EvalContext evalContext) {
    return getItemIds().stream().map(itemId -> evalContext.mapTo(itemId, true)).collect(toList());
  }

}
