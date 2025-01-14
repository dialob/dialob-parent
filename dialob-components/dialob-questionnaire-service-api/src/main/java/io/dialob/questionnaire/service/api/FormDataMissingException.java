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
package io.dialob.questionnaire.service.api;

import edu.umd.cs.findbugs.annotations.NonNull;

public class FormDataMissingException extends RuntimeException {

  private final String formId;
  private final String formRev;

  public FormDataMissingException(@NonNull String formId, String formRev) {
    super("Form '" + formId + (formRev != null ? "' rev '" + formRev : "'") + " cannot be loaded.");
    this.formId = formId;
    this.formRev = formRev;
  }

  @NonNull
  public String getFormId() {
    return formId;
  }

  public String getFormRev() {
    return formRev;
  }
}
