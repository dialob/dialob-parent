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
package io.dialob.session.engine;

import io.dialob.api.form.Form;
import io.dialob.db.spi.exceptions.DatabaseException;
import io.dialob.questionnaire.service.api.FormDataMissingException;
import io.dialob.questionnaire.service.api.InvalidFormException;
import io.dialob.questionnaire.service.api.session.FormFinder;
import io.dialob.session.engine.program.DialobProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class QuestionnaireDialobProgramService implements DialobProgramService {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuestionnaireDialobProgramService.class);

  private final FormFinder formFinder;

  private final DialobProgramFromFormCompiler programFromFormCompiler;

  public static Builder newBuilder() {
    return new Builder();
  }

  QuestionnaireDialobProgramService(@Nonnull FormFinder formDatabase, @Nonnull DialobProgramFromFormCompiler programFromFormCompiler) {
    this.formFinder = formDatabase;
    this.programFromFormCompiler = programFromFormCompiler;
  }

  @Override
  @Nonnull
  public DialobProgram findByFormId(@Nonnull String formId) {
    return findByFormIdAndRev(formId, null);
  }

  @Nonnull
  @Override
  public DialobProgram findByFormIdAndRev(@Nonnull String formId, String formRev) {
    Form formDocument;
    try {
      formDocument = formFinder.findForm(formId, formRev);
    } catch (DatabaseException e) {
      LOGGER.debug("Could not load form {}: {}", formId, e.getMessage());
      throw new FormDataMissingException(formId, formRev);
    }
    try {
      LOGGER.info("Compiling form document {} rev {}", formId, formRev);
      return this.programFromFormCompiler.compileForm(formDocument);
    } catch (DialobProgramErrorsException e) {
      throw new InvalidFormException("Form is not usable", e);
    }

  }

  public static class Builder {

    private FormFinder formDatabase;

    private DialobProgramFromFormCompiler programFromFormCompiler;

    private Builder() {}

    public Builder setFormDatabase(FormFinder formDatabase) {
      this.formDatabase = formDatabase;
      return this;
    }

    public Builder setProgramFromFormCompiler(DialobProgramFromFormCompiler programFromFormCompiler) {
      this.programFromFormCompiler = programFromFormCompiler;
      return this;
    }

    public QuestionnaireDialobProgramService build() {
      return new QuestionnaireDialobProgramService(formDatabase, programFromFormCompiler);
    }
  }
}
