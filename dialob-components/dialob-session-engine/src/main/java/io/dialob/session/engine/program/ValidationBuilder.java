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
package io.dialob.session.engine.program;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.form.FormValidationError;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.expr.arith.ImmutableIsActiveOperator;
import io.dialob.session.engine.program.expr.arith.ImmutableIsDisabledOperator;
import io.dialob.session.engine.program.expr.arith.Operators;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.program.model.ImmutableError;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class ValidationBuilder extends AbstractItemBuilder<ValidationBuilder, QuestionBuilder> {

  private final String errorCode;
  private Expression disabledExpression;

  private boolean prototype;
  private String when;

  public ValidationBuilder(QuestionBuilder questionBuilder, String errorCode) {
    super(questionBuilder.getProgramBuilder(), questionBuilder, questionBuilder.getHoistingGroup().orElse(null), IdUtils.toString(questionBuilder.getId()) + ":" + errorCode);
    this.errorCode = errorCode;
  }

  private ItemId getQuestionId() {
    return getParent().getId();
  }

  public ValidationBuilder setPrototype(boolean prototype) {
    this.prototype = prototype;
    return this;
  }

  @Override
  public ValidationBuilder setActiveWhen(@Nullable String when) {
    if (StringUtils.isBlank(when)) {
      this.when = null;
      return this;
    }
    this.when = when;
    ItemId questionId = getParent().getId();
    compileExpression(when, expression -> {
      if (expression.getValueType() == ValueType.BOOLEAN) {
        this.activeWhen = Operators.and(
          ImmutableIsActiveOperator.builder().itemId(questionId).build(),
          expression
        );
      } else {
        this.activeWhen = expression;
      }
    }, getActiveWhenExpressionErrorType(), Optional.empty());
    this.disabledExpression = ImmutableIsDisabledOperator.builder().itemId(questionId).build();
    return this;
  }

  @Override
  public Map<String, ItemId> getAliases() {
    var map = new HashMap<>(getParent().getAliases());
    map.put("answer", getQuestionId());
    return Map.copyOf(map);
  }

  @Override
  protected void afterExpressionCompilation(Consumer<FormValidationError> errorConsumer) {
    requireBooleanExpression(activeWhen, getActiveWhenExpressionErrorType(), errorConsumer);
    if (this.when == null) {
      return;
    }
    getParent().addError(
      ImmutableError.builder()
        .itemId(getQuestionId())
        .code(errorCode)
        .isPrototype(prototype)
        .validationExpression(activeWhen)
        .disabledExpression(disabledExpression)
        .label(createLabelOperator(label))
        .build()
    );
  }

  @Override
  @NonNull
  protected FormValidationError.Type getActiveWhenExpressionErrorType() {
    return FormValidationError.Type.VALIDATION;
  }
}
