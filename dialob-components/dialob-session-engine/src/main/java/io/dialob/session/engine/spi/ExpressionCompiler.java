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
package io.dialob.session.engine.spi;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.FormValidationError;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.session.model.ItemId;

import java.util.Optional;
import java.util.function.Consumer;

public interface ExpressionCompiler {

  boolean compile(@NonNull ItemId itemId,
                  @NonNull String expression,
                  @NonNull AliasesProvider aliasesProvider,
                  @NonNull Consumer<Expression> consumer,
                  @NonNull FormValidationError.Type type, Optional<Integer> index);

}
