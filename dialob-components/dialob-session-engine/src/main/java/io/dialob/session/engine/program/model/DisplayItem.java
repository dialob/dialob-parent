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
package io.dialob.session.engine.program.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DisplayItem extends Item {

  @Nullable
  String getView();

  @Nullable
  Map<String, @org.checkerframework.checker.nullness.qual.Nullable Object> getProps();

  Optional<Expression> getClassName();

  Optional<Expression> getActiveExpression();

  Optional<Expression> getRequiredExpression();

  Optional<Expression> getDisabledExpression();

  Optional<Expression> getLabelExpression();

  Optional<Expression> getDescriptionExpression();

  @NonNull
  List<Error> getErrors();

}
