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
package io.dialob.session.engine.program.expr;

import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.ProgramBuilderException;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class CannotReduceTypeException extends ProgramBuilderException {
  private final ValueType valueType;

  public CannotReduceTypeException(String message, ValueType valueType) {
    super(message);
    this.valueType = valueType;
  }

  @Override
  public List<Object> getArgs() {
    return Collections.singletonList(getValueType());
  }

}
