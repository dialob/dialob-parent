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
package io.dialob.function;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.rule.parser.api.ValueType;
import org.apache.commons.lang3.Strings;
import org.immutables.value.Value;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Value.Builder
@Value.Style(
  isSetOnBuilder = true,
  jakarta = true,
  jdkOnly = true,
  jdk9Collections = true,
  overshadowImplementation = true,
  visibility = Value.Style.ImplementationVisibility.PACKAGE
)
record ConfiguredFunction(
  String functionName,

  String staticMethodName,

  ValueType returnType,

  List<ValueType> argumentValueTypes,

  List<Class<?>> argumentTypes,

  Class<?> functionImplementationClass,

  boolean isAsync,

  @Nullable
  Predicate<ValueType[]> argumentMatcher,

  @Nullable
  String canonicalName

) {

  static class Builder extends ConfiguredFunctionBuilder {
  }

  public ConfiguredFunction {
    canonicalName = Objects.requireNonNullElseGet(canonicalName, () -> functionImplementationClass.getCanonicalName() + "." + staticMethodName);
    argumentMatcher = Objects.requireNonNullElse(argumentMatcher, argTypes -> argumentValueTypes().equals(Arrays.asList(argTypes)));
  }

  public boolean doesMatch(String canonicalFunctionName, final List<?> args) {
    if (Strings.CS.equalsAny(canonicalFunctionName, functionName(), canonicalName())) {
      final var argumentTypes = argumentTypes();
      for (int i = 0; i < args.size(); i++) {
        if (argumentTypes.size() < i
          || (args.get(i) != null && !argumentTypes.get(i).isAssignableFrom(args.get(i).getClass()))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

}

