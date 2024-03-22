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
package io.dialob.rest.type;

import io.dialob.api.rest.Errors;
import edu.umd.cs.findbugs.annotations.NonNull;

public class ApiException extends RuntimeException {

  private final Errors errors;

  public ApiException(@NonNull Errors errors) {
    super(errors.getMessage());
    this.errors = errors;
  }

  @NonNull
  public Errors getErrors() {
    return errors;
  }

  @Override
  public String toString() {
    return "ApiException{" +
      "errors=" + errors +
      '}';
  }

}
