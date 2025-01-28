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
package io.dialob.form.service.api.repository;

import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AbstractExpressionsVisitorTest {

  @Test
  void emptyDocumentShouldNotHaveExpressions() {
    Form document = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test").build()).build();
    VisitableForm.makeVisitableForm(document).accept(new AbstractExpressionsVisitor() {
      @Override
      protected void expression(String id, String classifier, String expression) {
        Assertions.fail("Should not reach");
      }

      @Override
      protected void condition(String id, String classifier, String condition) {
        Assertions.fail("Should not reach");
      }
    });
  }


}
