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
package io.dialob.form.service.api.repository;

import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.Mockito.*;

class VisitableFormTest {

  @Test
  public void shouldCallVisitorInOrder() throws Exception {
    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test").build()).build();
    FormVisitor formVisitor = Mockito.mock(FormVisitor.class);

    VisitableForm.makeVisitableForm(form).accept(formVisitor);

    InOrder inOrder = inOrder(formVisitor);

    inOrder.verify(formVisitor).start();
    inOrder.verify(formVisitor).visitForm(form);
    inOrder.verify(formVisitor).visitFormMetadata(form.getMetadata());
    inOrder.verify(formVisitor).startFormItems();
    inOrder.verify(formVisitor).endFormItems();
    inOrder.verify(formVisitor).startFormVariables();
    inOrder.verify(formVisitor).endFormVariables();
    inOrder.verify(formVisitor).startValueSets();
    inOrder.verify(formVisitor).endValueSets();
    inOrder.verify(formVisitor).end();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldCallVisitorInOrder2() throws Exception {
    Form form = ImmutableForm.builder()
      .metadata(ImmutableFormMetadata.builder().label("test").build())
      .putData("question1", ImmutableFormItem.builder().id("question1").type("text").build())
      .putData("group1", ImmutableFormItem.builder().id("group1").type("group").addItems("question1","nonexisting").build())
      .putData("page1", ImmutableFormItem.builder().id("page1").type("group").addItems("group1").build())
      .putData("group2", ImmutableFormItem.builder().id("group2").type("group").addItems("question2").build())
      .putData("page2", ImmutableFormItem.builder().id("page2").type("group").addItems("group2").build())
      .putData("pagenotexists", ImmutableFormItem.builder().id("pagenotexists").type("group").addItems("group1").build())
      .putData("questionnaire", ImmutableFormItem.builder().id("questionnaire").type("questionnaire").addItems("page1","page2").build())
      .build();
    FormVisitor formVisitor = Mockito.mock(FormVisitor.class);
    FormItemVisitor formItemVisitor = Mockito.mock(FormItemVisitor.class);

    when(formVisitor.startFormItems()).thenReturn(Optional.of(formItemVisitor));

    VisitableForm.makeVisitableForm(form).accept(formVisitor);

    InOrder inOrder = inOrder(formVisitor, formItemVisitor);
    inOrder.verify(formVisitor).start();
    inOrder.verify(formVisitor).visitForm(form);
    inOrder.verify(formVisitor).visitFormMetadata(form.getMetadata());
    inOrder.verify(formVisitor).startFormItems();
    inOrder.verify(formItemVisitor).start();
    inOrder.verify(formItemVisitor).visitQuestionnaireItem(any());
    inOrder.verify(formItemVisitor).startValidations();
    inOrder.verify(formItemVisitor).endValidations();
    inOrder.verify(formItemVisitor).end();
    inOrder.verify(formItemVisitor).start();
    inOrder.verify(formItemVisitor).visitPage(any());
    inOrder.verify(formItemVisitor).startValidations();
    inOrder.verify(formItemVisitor).endValidations();
    inOrder.verify(formItemVisitor).end();
    inOrder.verify(formItemVisitor).start();
    inOrder.verify(formItemVisitor).visitPage(any());
    inOrder.verify(formItemVisitor).startValidations();
    inOrder.verify(formItemVisitor).endValidations();
    inOrder.verify(formItemVisitor).end();
    inOrder.verify(formItemVisitor).start();
    inOrder.verify(formItemVisitor).visitGroup(any());
    inOrder.verify(formItemVisitor).startValidations();
    inOrder.verify(formItemVisitor).endValidations();
    inOrder.verify(formItemVisitor).end();
    inOrder.verify(formItemVisitor).start();
    inOrder.verify(formItemVisitor).visitGroup(any());
    inOrder.verify(formItemVisitor).startValidations();
    inOrder.verify(formItemVisitor).endValidations();
    inOrder.verify(formItemVisitor).end();
    inOrder.verify(formItemVisitor).start();
    inOrder.verify(formItemVisitor).visitQuestion(any());
    inOrder.verify(formItemVisitor).startValidations();
    inOrder.verify(formItemVisitor).endValidations();
    inOrder.verify(formItemVisitor).end();
    inOrder.verify(formVisitor).endFormItems();
    inOrder.verify(formVisitor).startFormVariables();
    inOrder.verify(formVisitor).endFormVariables();
    inOrder.verify(formVisitor).startValueSets();
    inOrder.verify(formVisitor).endValueSets();
    inOrder.verify(formVisitor).end();
    inOrder.verifyNoMoreInteractions();
    verifyNoMoreInteractions(formVisitor, formItemVisitor);
  }

}
