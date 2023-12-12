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
package io.dialob.db.jdbc;

import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.ResysSecurityConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public abstract class AbstractCRUDDatabaseTest implements JdbcBackendTest {

  @BeforeEach
  void cleanup() {
    getJdbcTemplate().update("delete from questionnaire");
    getJdbcTemplate().update("delete from form_document");
  }

  @Test
  public void saveAndLoadAndDeleteForm() {
    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test form öä").build()).build();
    Form form2 = getJdbcFormDatabase().save(getCurrentTenant().getId(), form);
    assertNotNull(form2.getId());
    assertNotNull(form2.getRev());
    Form form3 = getJdbcFormDatabase().findOne(getCurrentTenant().getId(), form2.getId());
    assertEquals(form2.getId(), form3.getId());
    assertEquals(form2.getRev(), form3.getRev());
    assertEquals(form2.getMetadata().getLabel(), form3.getMetadata().getLabel());
    getJdbcFormDatabase().delete(getCurrentTenant().getId(), form2.getId());
    assertFalse(getJdbcFormDatabase().exists(getCurrentTenant().getId(), form2.getId()));
    resetTenant();
  }


  @Test
  public void saveAndUpdate() {
    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test form").build()).build();
    Form form2 = getJdbcFormDatabase().save(getCurrentTenant().getId(), form);

    form2 = ImmutableForm.builder().from(form2).putData("questionnaire", ImmutableFormItem.builder()
      .id("questionnaire")
      .type("questionnaire")
      .build()).build();

    getJdbcFormDatabase().save(getCurrentTenant().getId(), form2);
    Form form3 = getJdbcFormDatabase().findOne(getCurrentTenant().getId(), form2.getId());

    assertNotNull(form3.getData().get("questionnaire"));
  }


  @Test
  public void saveAndFetchList() {
    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test form").build()).build();
    Form form2 = getJdbcFormDatabase().save(getCurrentTenant().getId(), form);

    form2 = ImmutableForm.builder().from(form2).putData("questionnaire", ImmutableFormItem.builder()
      .id("questionnaire")
      .type("questionnaire")
      .build()).build();

    getJdbcFormDatabase().save(getCurrentTenant().getId(), form2);
    List<FormDatabase.FormMetadataRow> list = new ArrayList<>();
    getJdbcFormDatabase().findAllMetadata(getCurrentTenant().getId(), null, list::add);


    assertEquals(1, list.size());
    assertEquals(32, list.get(0).getId().length());
  }


  @Test
  public void saveAndFetchListTenantScoped() {
    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test form").build())
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .build())
      .build();

    setActiveTenant("t1");
    getJdbcFormDatabase().save(getCurrentTenant().getId(), ImmutableForm.builder().from(form)
      .id(null).build());
    getJdbcFormDatabase().save(getCurrentTenant().getId(), ImmutableForm.builder().from(form)
      .id(null).build());

    setActiveTenant("t2");
    getJdbcFormDatabase().save(getCurrentTenant().getId(), ImmutableForm.builder().from(form)
      .id(null).build());
    getJdbcFormDatabase().save(getCurrentTenant().getId(), ImmutableForm.builder().from(form)
      .id(null).build());
    getJdbcFormDatabase().save(getCurrentTenant().getId(), ImmutableForm.builder().from(form)
      .id(null).build());


    setActiveTenant("t1");
    List<FormDatabase.FormMetadataRow> list1 = new ArrayList<>();
    getJdbcFormDatabase().findAllMetadata(getCurrentTenant().getId(), null, list1::add);

    setActiveTenant("t2");
    List<FormDatabase.FormMetadataRow> list2 = new ArrayList<>();
    getJdbcFormDatabase().findAllMetadata(getCurrentTenant().getId(), null, list2::add);


    assertEquals(2, list1.size());
    assertEquals(3, list2.size());
  }

  @Test
  public void saveAndUpdateQuestionnaireWithoutTenant() {
    setActiveTenant(ResysSecurityConstants.DEFAULT_TENANT.getId());
    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test form").build())
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .build())
      .build();
    form = getJdbcFormDatabase().save(getCurrentTenant().getId(), form);
    assertNotNull(form.getId());

    Questionnaire q = getQuestionnaireDatabase().save(getCurrentTenant().getId(), ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder()
      .formId(form.getId()).build()).build());

    assertEquals("1", q.getRev());

    q = getQuestionnaireDatabase().save(getCurrentTenant().getId(), q);
    assertEquals("2", q.getRev());

  }


  @Test
  public void saveAndUpdateQuestionnaireWithTenant() {
    setActiveTenant("12341234-1234-1234-1234-123412341234");
    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test form").build())
      .putData("questionnaire", ImmutableFormItem.builder()
        .id("questionnaire")
        .type("questionnaire")
        .build())
      .build();
    form = getJdbcFormDatabase().save(getCurrentTenant().getId(), form);
    assertNotNull(form.getId());

    Questionnaire q = getQuestionnaireDatabase().save(getCurrentTenant().getId(), ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder()
      .formId(form.getId()).build()).build());

    assertEquals("1", q.getRev());

    // DEFAULT tenant -> no tenant scoped save
    setActiveTenant(ResysSecurityConstants.DEFAULT_TENANT.getId());
    q = getQuestionnaireDatabase().save(getCurrentTenant().getId(), q);
    assertEquals("2", q.getRev());

    // fails when using wrong tenant
    Questionnaire q2 = q;
    setActiveTenant("12341234-1234-1234-1234-123412341235");
    assertThrows(DocumentNotFoundException.class, () -> getQuestionnaireDatabase().save(getCurrentTenant().getId(), q2));
  }

  @Test
  public void saveAndFetchMetadata() {

    setActiveTenant("12341234-1234-1234-1234-123412341236");

    Form form = ImmutableForm.builder().metadata(ImmutableFormMetadata.builder().label("test form").build()).build();
    Form form2 = getJdbcFormDatabase().save(getCurrentTenant().getId(), form);

    form2 = ImmutableForm.builder().from(form2).putData("questionnaire", ImmutableFormItem.builder()
      .id("questionnaire")
      .type("questionnaire")
      .build()).build();

    form2 = getJdbcFormDatabase().save(getCurrentTenant().getId(), form2);

    Questionnaire questionnaire = getQuestionnaireDatabase()
      .save(getCurrentTenant().getId(),
        ImmutableQuestionnaire.builder()
          .metadata(ImmutableQuestionnaireMetadata.builder()
            .formId(form2.getId())
            .owner("me")
            .build()).build());

    QuestionnaireDatabase.MetadataRow metadata = getQuestionnaireDatabase().findMetadata(null, questionnaire.getId());

    assertEquals("12341234-1234-1234-1234-123412341236", metadata.getValue().getTenantId());
    assertEquals("me", metadata.getValue().getOwner());

    List<QuestionnaireDatabase.MetadataRow> rows = new ArrayList<>();
    getQuestionnaireDatabase().findAllMetadata("12341234-1234-1234-1234-123412341236", null, null, null, null, null, rows::add);
    assertEquals(1, rows.size());

    rows.clear();
    getQuestionnaireDatabase().findAllMetadata("12341234-1234-1234-1234-123412341236", "he", null, null, null, null, rows::add);
    assertTrue(rows.isEmpty());

    rows.clear();
    getQuestionnaireDatabase().findAllMetadata("12341234-1234-1234-1234-123412341236", null, form2.getId(), null, null, null, rows::add);
    assertFalse(rows.isEmpty());

    rows.clear();
    getQuestionnaireDatabase().findAllMetadata("ffffffff-ffff-ffff-ffff-ffffffffffff", null, null, null, null, null, rows::add);
    assertTrue(rows.isEmpty());

    rows.clear();
    getQuestionnaireDatabase().findAllMetadata("12341234-1234-1234-1234-123412341236", "me", form2.getId(), null, null, null, rows::add);
    assertEquals(1, rows.size());
  }


  @Test
  public void findMetadataShouldThrowDocumentNotFoundException() {
    assertThrows(DocumentNotFoundException.class, () -> getQuestionnaireDatabase().findMetadata(null, "12341234123412341234123412341236"));
    assertThrows(DocumentNotFoundException.class, () -> getQuestionnaireDatabase().findMetadata(null, null));
  }

}
