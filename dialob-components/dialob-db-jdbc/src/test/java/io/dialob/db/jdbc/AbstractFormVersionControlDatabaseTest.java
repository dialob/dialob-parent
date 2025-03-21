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
package io.dialob.db.jdbc;

import io.dialob.api.form.*;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import io.dialob.db.spi.exceptions.DocumentLockedException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.form.service.api.FormDatabase;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public abstract class AbstractFormVersionControlDatabaseTest implements JdbcBackendTest {

  @AfterEach
  public void cleanup() {
    final JdbcTemplate template = getJdbcTemplate();
    LOGGER.trace("delete from questionnaire : {}", template.update("delete from questionnaire"));
    LOGGER.trace("delete from form_rev : {}", template.update("delete from form_rev where ref_name is not null"));
    LOGGER.trace("delete from form_rev : {}", template.update("delete from form_rev"));
    LOGGER.trace("delete from form : {}", template.update("delete from form"));
    LOGGER.trace("delete from form_rev_archive : {}", template.update("delete from form_rev_archive"));
    LOGGER.trace("delete from form_archive : {}", template.update("delete from form_archive"));
    LOGGER.trace("delete from form_document : {}", template.update("delete from form_document"));
    resetTenant();
  }

  @Test
  void shouldSaveAndLoadForm() {
    Form form = ImmutableForm.builder().name("shouldSaveAndLoadForm").metadata(ImmutableFormMetadata.builder().label("test form").build()).build();

    form = getJdbcFormVersionControlDatabase().getFormDatabase().save(getCurrentTenant().getId(), form);
    assertNotNull(form.getId());
    assertNotNull(form.getRev());
    LOGGER.debug("form id : {}", form.getId());

    Form form2 = getJdbcFormDatabase().findOne(getCurrentTenant().getId(), form.getId(), null);
    assertNotNull(form2);
  }
  @Test
  void shouldRejectFormWithExistingName() {
    Form form1 = ImmutableForm.builder().name("shouldSaveAndLoadForm").metadata(ImmutableFormMetadata.builder().label("test form").build()).build();
    Form form2 = ImmutableForm.builder().name("shouldSaveAndLoadForm").metadata(ImmutableFormMetadata.builder().label("test form").build()).build();

    getJdbcFormVersionControlDatabase().getFormDatabase().save(getCurrentTenant().getId(), form1);
    assertThrows(DocumentConflictException.class, () -> getJdbcFormVersionControlDatabase().getFormDatabase().save(getCurrentTenant().getId(), form2));
  }

  @Test
  void shouldCreateControlledFormEntryWhenNewFormIsCreated() {
    Form form = ImmutableForm.builder()
      .name("uusi-lomake-1")
      .metadata(ImmutableFormMetadata.builder()
        .label("uusi lomake 1")
        .build()).build();

    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    form = database.save(getCurrentTenant().getId(), form);
    assertNotNull(form.getId());
    assertNotNull(form.getRev());

    assertTrue(database.exists(getCurrentTenant().getId(), "uusi-lomake-1"));
  }


  @Test
  void shouldNotUpdateTaggedForm() {
    Form form = ImmutableForm.builder()
      .name("uusi-lomake-2")
      .metadata(ImmutableFormMetadata.builder()
        .label("uusi lomake 2")
        .build()).build();

    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    Form form2 = database.save(getCurrentTenant().getId(), form);
    assertNotNull(form2.getId());
    assertNotNull(form2.getRev());

    Form newForm = database.findOne(getCurrentTenant().getId(), "uusi-lomake-2");
    assertEquals(form2.getId(), newForm.getId());
    controlDatabase.createTagOnLatest(getCurrentTenant().getId(), "uusi-lomake-2", "v1", null, false, "user-1");

    assertThrows(DocumentLockedException.class, () -> database.save(getCurrentTenant().getId(), form2));

    // tag twice
    assertThrows(DocumentConflictException.class, () -> controlDatabase.createTagOnLatest(getCurrentTenant().getId(), "uusi-lomake-2", "v1", null, false, "user-1"));
  }

  @Test
  void shouldFindAllTags() {
    Form form = ImmutableForm.builder()
      .name("shouldFindAllTags")
      .metadata(ImmutableFormMetadata.builder()
        .label("shouldFindAllTags")
        .build()).build();

    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    Form form2 = database.save(getCurrentTenant().getId(), form);
    assertNotNull(form2.getId());
    assertNotNull(form2.getRev());

    controlDatabase.createTagOnLatest(getCurrentTenant().getId(), "shouldFindAllTags", "v1", null, false, "user-1");
    List<FormTag> formTagList1 = controlDatabase.findTags(getCurrentTenant().getId(), "shouldFindAllTags", null);
    List<FormTag> formTagList2 = controlDatabase.findTags(getCurrentTenant().getId(), form2.getId(), null);
    Assertions.assertEquals(1, formTagList1.size());
    Assertions.assertEquals(1, formTagList2.size());
    Assertions.assertIterableEquals(formTagList1, formTagList2);

    assertFalse(controlDatabase.isName(getCurrentTenant().getId(), form2.getId()));
    assertTrue(controlDatabase.isName(getCurrentTenant().getId(), "shouldFindAllTags"));

    List<FormDatabase.FormMetadataRow> list = new ArrayList<>();
    controlDatabase.getFormDatabase().findAllMetadata(getCurrentTenant().getId(), null, list::add);
    assertEquals(1, list.size());
    FormDatabase.FormMetadataRow row = list.get(0);
    assertEquals("shouldFindAllTags", row.getValue().getLabel());
    assertEquals("shouldFindAllTags", row.getId());
    controlDatabase.updateLabel(getCurrentTenant().getId(), "shouldFindAllTags", "renamed one");

    list.clear();
    controlDatabase.getFormDatabase().findAllMetadata(getCurrentTenant().getId(), null, list::add);
    assertEquals(1, list.size());
    row = list.get(0);
    assertEquals("renamed one", row.getValue().getLabel());
    assertEquals("shouldFindAllTags", row.getId());


    assertFalse(controlDatabase.deleteTag(getCurrentTenant().getId(), "shouldFindAllTags", "v1"));
    assertFalse(controlDatabase.findTags(getCurrentTenant().getId(), "shouldFindAllTags", null).isEmpty());
    assertTrue(controlDatabase.findTags(getCurrentTenant().getId(), "v1", null).isEmpty());
  }

  @Test
  void shouldCreateTagWithNullableCreator() {
    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    form1 = database.save(getCurrentTenant().getId(), form1);
    assertNotNull(form1.getId());
    assertNotNull(form1.getRev());

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag1", "First tag without creator", form1.getId(), FormTag.Type.NORMAL, null );
    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", "Second tag with creator", form1.getId(), FormTag.Type.NORMAL, "user-1");

    assertEquals(form1.getId(), controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag1").get().getFormId());
    assertNull(controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag1").get().getCreator());
    assertEquals(form1.getId(), controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag2").get().getFormId());
    assertEquals("user-1", controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag2").get().getCreator());
  }

  @Test
  void shouldFindTag() {
    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();
    Form form2 = ImmutableForm.builder()
      .name("form2")
      .metadata(ImmutableFormMetadata.builder()
        .label("form2")
        .build()).build();

    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    form1 = database.save(getCurrentTenant().getId(), form1);
    assertNotNull(form1.getId());
    assertNotNull(form1.getRev());

    form2 = database.save(getCurrentTenant().getId(), form2);
    assertNotNull(form1.getId());
    assertNotNull(form1.getRev());

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag1", "First tag with description", form1.getId(), FormTag.Type.NORMAL, "user-1");
    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", "Second tag with description", form2.getId(), FormTag.Type.NORMAL, "user-1");

    assertEquals(form1.getId(), controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag1").get().getFormId());
    assertEquals("First tag with description", controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag1").get().getDescription());
    assertEquals(form2.getId(), controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag2").get().getFormId());
    assertEquals("Second tag with description", controlDatabase.findTag(getCurrentTenant().getId(), "form1", "tag2").get().getDescription());

    List<FormTag> tags = controlDatabase.queryTags(getCurrentTenant().getId(), null, null,null, FormTag.Type.NORMAL);
    assertEquals(2, tags.size());
    tags = controlDatabase.queryTags(getCurrentTenant().getId(), "form1", null,null, FormTag.Type.NORMAL);
    assertEquals(2, tags.size());
    tags = controlDatabase.queryTags(getCurrentTenant().getId(), null, null,"tag2", FormTag.Type.NORMAL);
    assertEquals(1, tags.size());
    tags = controlDatabase.queryTags(getCurrentTenant().getId(), null, form1.getId(),null, FormTag.Type.NORMAL);
    assertEquals(1, tags.size());
    tags = controlDatabase.queryTags(getCurrentTenant().getId(), null, form1.getId(),"tag2", FormTag.Type.NORMAL);
    assertEquals(0, tags.size());
    tags = controlDatabase.queryTags(getCurrentTenant().getId(), null, form1.getId(),"tag1", FormTag.Type.NORMAL);
    assertEquals(1, tags.size());
    tags = controlDatabase.queryTags(getCurrentTenant().getId(), "form1", form1.getId(),"tag1", FormTag.Type.NORMAL);
    assertEquals(1, tags.size());
  }

  @Test
  void shouldResolveFormIdOnQuestionnaireToLastest() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    final QuestionnaireDatabase questionnaireDatabase = getQuestionnaireDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    form1 = database.save(getCurrentTenant().getId(), form1);

    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("form1").build()).build();

    questionnaire = questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire);
    assertEquals(form1.getId(), questionnaire.getMetadata().getFormId());
    assertNull(questionnaire.getMetadata().getFormRev());
  }

  @Test
  void shouldResolveFormIdOnQuestionnaireToTag() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    final QuestionnaireDatabase questionnaireDatabase = getQuestionnaireDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    Form form2 = ImmutableForm.builder()
      .name("form2")
      .metadata(ImmutableFormMetadata.builder()
        .label("form2")
        .build()).build();

    form1 = database.save(getCurrentTenant().getId(), form1);
    form2 = database.save(getCurrentTenant().getId(), form2);

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form2.getId(), FormTag.Type.NORMAL, "user-1");


    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("form1").formRev("tag2").build()).build();

    questionnaire = questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire);
    assertEquals(form2.getId(), questionnaire.getMetadata().getFormId());
    assertNull(questionnaire.getMetadata().getFormRev());
  }


  @Test
  void shouldBeAbleToUpdateLastest() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    Form form2 = ImmutableForm.builder()
      .name("form2")
      .metadata(ImmutableFormMetadata.builder()
        .label("form2")
        .build()).build();

    form1 = database.save(getCurrentTenant().getId(), form1);
    form2 = database.save(getCurrentTenant().getId(), form2);

    assertFalse(controlDatabase.updateLatest(getCurrentTenant().getId(), "form1", ImmutableFormTag.builder()
      .formName("form1")
      .name("latest")
      .build()));

    assertEquals(form1.getId(), controlDatabase.findTag(getCurrentTenant().getId(), "form1", "latest").get().getFormId());

    assertTrue(controlDatabase.updateLatest(getCurrentTenant().getId(), "form1", ImmutableFormTag.builder()
      .formName("form1")
      .name("latest")
      .formId(form2.getId())
      .build()));

    assertEquals(form2.getId(), controlDatabase.findTag(getCurrentTenant().getId(), "form1", "latest").get().getFormId());

  }
  @Test
  void shouldSnapshotLatestFormOnTagging() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    form1 = database.save(getCurrentTenant().getId(), form1);

    controlDatabase.createTagOnLatest(getCurrentTenant().getId(), "form1", "v1", null, true, "user-1");
    String newformId = controlDatabase.findTag(getCurrentTenant().getId(), "form1", "v1").get().getFormId();
    assertNotEquals(form1.getId(), newformId);
    assertEquals(form1.getId(), controlDatabase.findTag(getCurrentTenant().getId(), "form1", "latest").get().getFormId());


  }

  @Test
  void missingTagShouldntRollbackTransaction() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    final QuestionnaireDatabase questionnaireDatabase = getQuestionnaireDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    form1 = database.save(getCurrentTenant().getId(), form1);

    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId(form1.getId()).formRev("LATEST").build()).build();

    questionnaire = questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire);
    assertEquals(Questionnaire.Metadata.Status.NEW, questionnaire.getMetadata().getStatus());
    assertNull(questionnaire.getMetadata().getFormRev());
  }


  @Test
  void shouldBeAbleToSameTagNameInSeparateTenants() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    final QuestionnaireDatabase questionnaireDatabase = getQuestionnaireDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    Form form2 = form1;

    setActiveTenant("tenant-1");
    form1 = database.save(getCurrentTenant().getId(), form1);

    setActiveTenant("tenant-2");
    form2 = database.save(getCurrentTenant().getId(), form2);

    setActiveTenant("tenant-1");
    // no cross tenant tags
    assertFalse(controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form2.getId(), FormTag.Type.NORMAL, "user-1").isPresent());
    assertTrue(controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form1.getId(), FormTag.Type.NORMAL, "user-1").isPresent());

    setActiveTenant("tenant-2");
    assertTrue(controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form2.getId(), FormTag.Type.NORMAL, "user-1").isPresent());

    setActiveTenant("tenant-1");
    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("form1").formRev("tag2").build()).build();

    questionnaire = questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire);
    assertEquals(form1.getId(), questionnaire.getMetadata().getFormId());
    assertNull(questionnaire.getMetadata().getFormRev());
  }

  @Test
  void cannotUseCrossTenantForms() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    final QuestionnaireDatabase questionnaireDatabase = getQuestionnaireDatabase();

    setActiveTenant("tenant-1");

    Form form1 = database.save(getCurrentTenant().getId(), ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build());

    setActiveTenant("tenant-2");
    Form form2 = database.save(getCurrentTenant().getId(), ImmutableForm.builder()
      .name("form2")
      .metadata(ImmutableFormMetadata.builder()
        .label("form2")
        .build()).build());

    setActiveTenant("tenant-1");
    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form2.getId(), FormTag.Type.NORMAL, "user-1");

    setActiveTenant("tenant-2");
    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form2.getId(), FormTag.Type.NORMAL, "user-1");

    Assertions.assertThrows(DocumentNotFoundException.class, () -> {
      setActiveTenant("tenant-2");
      Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId("form1").formRev("tag2").build()).build();
      questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire);
    });
    Assertions.assertThrows(DocumentNotFoundException.class, () -> {
      setActiveTenant("tenant-2");
      Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId(form1.getId()).build()).build();
      questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire);
    });
    setActiveTenant("tenant-1");
    Questionnaire questionnaire = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder().formId(form1.getId()).build()).build();
    Questionnaire q = questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire);
    assertEquals("tenant-1", q.getMetadata().getTenantId());
  }


  @Test
  void deleteShouldHideFormFromListButRetainFormDocument() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    database.save(getCurrentTenant().getId(), form1);
    controlDatabase.createTagOnLatest(getCurrentTenant().getId(), "form1", "v1", null, true, "user-1");

    assertEquals((Integer)1, getJdbcTemplate().queryForObject("select count(*) from form_rev", Integer.class));
    assertEquals((Integer)1, getJdbcTemplate().queryForObject("select count(*) from form", Integer.class));
    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form_rev_archive", Integer.class));
    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form_archive", Integer.class));

    List<FormDatabase.FormMetadataRow> rows = new ArrayList<>();
    database.findAllMetadata(getCurrentTenant().getId(), null, rows::add);
    assertEquals(1, rows.size());

    boolean deleted = controlDatabase.delete(getCurrentTenant().getId(), "form1");
    assertTrue(deleted);

    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form_rev", Integer.class));
    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form", Integer.class));
    assertEquals((Integer)1, getJdbcTemplate().queryForObject("select count(*) from form_rev_archive", Integer.class));
    assertEquals((Integer)1, getJdbcTemplate().queryForObject("select count(*) from form_archive", Integer.class));

    rows.clear();
    database.findAllMetadata(getCurrentTenant().getId(), null, rows::add);
    assertEquals(0, rows.size());
  }

  @Test
  void shouldBePossibleToCreateFormWithSameNameAgainAfterDelete() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    database.save(getCurrentTenant().getId(), form1);

    boolean deleted = controlDatabase.delete(getCurrentTenant().getId(), "form1");
    assertTrue(deleted);

    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form", Integer.class));
    assertEquals((Integer)1, getJdbcTemplate().queryForObject("select count(*) from form_archive", Integer.class));

    database.save(getCurrentTenant().getId(), form1);

    List<FormDatabase.FormMetadataRow> rows = new ArrayList<>();
    database.findAllMetadata(getCurrentTenant().getId(), null, rows::add);
    assertEquals(1, rows.size());
    assertEquals("form1", rows.get(0).getId());

    deleted = controlDatabase.delete(getCurrentTenant().getId(), "form1");

    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form", Integer.class));
    assertEquals((Integer)2, getJdbcTemplate().queryForObject("select count(*) from form_archive", Integer.class));

    assertTrue(deleted);
  }
  @Test
  void shouldAccept128CharactersLongFormName() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    Form form1 = ImmutableForm.builder()
      .name("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    Form saved = database.save(getCurrentTenant().getId(), form1);

    assertNotNull(saved.getId());
  }

  @Test
  void shouldReject129CharactersLongFormName() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    Form form1 = ImmutableForm.builder()
      .name("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef+")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    Assertions.assertThrows(DocumentCorruptedException.class, () -> database.save(getCurrentTenant().getId(), form1));
  }

  @Test
  void shouldFindQuestionnairesByFormNameAndTag() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();
    final QuestionnaireDatabase questionnaireDatabase = getQuestionnaireDatabase();

    Form form1 = ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build();

    Form form2 = ImmutableForm.builder()
      .name("form2")
      .metadata(ImmutableFormMetadata.builder()
        .label("form2")
        .build()).build();

    setActiveTenant("tenant-1");
    form1 = database.save(getCurrentTenant().getId(), form1);

    setActiveTenant("tenant-2");
    form2 = database.save(getCurrentTenant().getId(), form2);

    setActiveTenant("tenant-1");
    assertTrue(controlDatabase.createTagOnLatest(getCurrentTenant().getId(), "form1", "tag2", null, true, "user-1").isPresent());

    setActiveTenant("tenant-2");
    assertTrue(controlDatabase.createTagOnLatest(getCurrentTenant().getId(), "form2", "tag2", null, true, "user-1").isPresent());

    setActiveTenant("tenant-1");
    Questionnaire questionnaire1 = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder()
      .formId("form1")
      .formRev("tag2").build()).build();
    Questionnaire questionnaire1L = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder()
      .formId("form1").build()).build();
    Questionnaire questionnaire2 = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder()
      .formId("form2")
      .formRev("tag2").build()).build();
    Questionnaire questionnaire2L = ImmutableQuestionnaire.builder().metadata(ImmutableQuestionnaireMetadata.builder()
      .formId("form2").build()).build();
    questionnaire1 = questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire1);
    assertNull(questionnaire1.getMetadata().getFormRev()); // not null?
    questionnaire1L = questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire1L);
    assertEquals(form1.getId(), questionnaire1L.getMetadata().getFormId());
    assertNull(questionnaire1L.getMetadata().getFormRev());

    setActiveTenant("tenant-2");
    questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire2);
    questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire2);
    questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire2L);
    questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire2L);
    questionnaireDatabase.save(getCurrentTenant().getId(), questionnaire2L);

    setActiveTenant("tenant-1");
    List<QuestionnaireDatabase.MetadataRow> result = new ArrayList<>();
    questionnaireDatabase.findAllMetadata(getCurrentTenant().getId(), null, null, "form1", null, null, result::add);
    assertEquals(2, result.size());
    result.clear();
    questionnaireDatabase.findAllMetadata(getCurrentTenant().getId(), null, null, "form1", "tag2", null, result::add);
    assertEquals(1, result.size());
    result.clear();

    setActiveTenant("tenant-2");
    questionnaireDatabase.findAllMetadata(getCurrentTenant().getId(), null, null, "form2", null, null, result::add);
    assertEquals(5, result.size());
    result.clear();
    questionnaireDatabase.findAllMetadata(getCurrentTenant().getId(), null, null, "form2", "tag2", null, result::add);
    assertEquals(2, result.size());
    result.clear();

  }


  @Test
  void shouldBeAbleToCreateMutableTags() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    setActiveTenant("tenant-1");
    Form form1 = database.save(getCurrentTenant().getId(), ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build());

    Form form2 = database.save(getCurrentTenant().getId(), ImmutableForm.builder()
      .name("form2")
      .metadata(ImmutableFormMetadata.builder()
        .label("form2")
        .build()).build());

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag1", null, form1.getId(), FormTag.Type.NORMAL, "user-1");
    controlDatabase.createTag(getCurrentTenant().getId(), "form2", "tag2", null, form2.getId(), FormTag.Type.NORMAL, "user-1");

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag1m", null, "tag1", FormTag.Type.MUTABLE, "user-1");
    controlDatabase.createTag(getCurrentTenant().getId(), "form2", "tag2m", null, "tag2", FormTag.Type.MUTABLE, "user-1");

    assertEquals((Integer)4, getJdbcTemplate().queryForObject("select count(*) from form_rev", Integer.class));
    List<FormTag> tags = getJdbcFormVersionControlDatabase().queryTags(
      getCurrentTenant().getId(), null, null, null, FormTag.Type.NORMAL
    );

    Assertions.assertEquals(2, tags.size());
    org.assertj.core.api.Assertions.assertThat(tags).extracting("formId").containsExactlyInAnyOrder(
      form1.getId(),
      form2.getId()
    );

    tags = getJdbcFormVersionControlDatabase().queryTags(
      getCurrentTenant().getId(), null, null, null, FormTag.Type.MUTABLE
    );
    Assertions.assertEquals(2, tags.size());
    org.assertj.core.api.Assertions.assertThat(tags).extracting("formName","name","formId","refName").containsExactlyInAnyOrder(
      tuple("form1","tag1m",form1.getId(),"tag1"),
      tuple("form2","tag2m",form2.getId(),"tag2")
    );

    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form_rev_archive", Integer.class));
    assertTrue(getJdbcFormVersionControlDatabase().deleteTag(getCurrentTenant().getId(), "form1", "tag1m"));
    assertEquals((Integer)1, getJdbcTemplate().queryForObject("select count(*) from form_rev_archive", Integer.class));

    tags = getJdbcFormVersionControlDatabase().queryTags(
      getCurrentTenant().getId(), null, null, null, FormTag.Type.MUTABLE
    );
    Assertions.assertEquals(1, tags.size());
    org.assertj.core.api.Assertions.assertThat(tags).extracting("formName","name","formId","refName").containsExactlyInAnyOrder(
      tuple("form2","tag2m",form2.getId(),"tag2")
    );
  }


  @Test
  void shouldBeAbleToUpdateMutableTags() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    setActiveTenant("tenant-1");
    Form form1 = database.save(getCurrentTenant().getId(), ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build());

    String form2 = controlDatabase.createSnapshot(getCurrentTenant().getId(), form1.getId());

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag1", null, form1.getId(), FormTag.Type.NORMAL, "user-1");
    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form2, FormTag.Type.NORMAL, "user-1");

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag1m", null, "tag1", FormTag.Type.MUTABLE, "user-1");
    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2m", null, "tag2", FormTag.Type.MUTABLE, "user-1");

    List<FormTag> tags = getJdbcFormVersionControlDatabase().queryTags(
      getCurrentTenant().getId(), null, null, null, FormTag.Type.MUTABLE
    );
    Assertions.assertEquals(2, tags.size());
    org.assertj.core.api.Assertions.assertThat(tags).extracting("formName","name","formId","refName").containsExactlyInAnyOrder(
      tuple("form1","tag1m",form1.getId(),"tag1"),
      tuple("form1","tag2m",form2,"tag2")
    );

    assertEquals((Integer)0, getJdbcTemplate().queryForObject("select count(*) from form_rev_archive", Integer.class));

    controlDatabase.moveTag(getCurrentTenant().getId(), ImmutableFormTag.builder()
      .formName("form1")
      .name("tag1m")
      .refName("tag2")
      .type(FormTag.Type.MUTABLE)
      .build());

    controlDatabase.moveTag(getCurrentTenant().getId(), ImmutableFormTag.builder()
      .formName("form1")
      .name("tag2m")
      .refName("tag1")
      .type(FormTag.Type.MUTABLE)
      .build());

    assertEquals((Integer)2, getJdbcTemplate().queryForObject("select count(*) from form_rev_archive", Integer.class));

    tags = getJdbcFormVersionControlDatabase().queryTags(
      getCurrentTenant().getId(), null, null, null, FormTag.Type.MUTABLE
    );
    Assertions.assertEquals(2, tags.size());
    org.assertj.core.api.Assertions.assertThat(tags).extracting("formName","name","formId","refName").containsExactlyInAnyOrder(
      tuple("form1","tag1m",form2,"tag2"),
      tuple("form1","tag2m",form1.getId(),"tag1")
    );
  }


  @Test
  void cannotMoveNormalTag() {
    final FormVersionControlDatabase controlDatabase = getJdbcFormVersionControlDatabase();
    final FormDatabase database = controlDatabase.getFormDatabase();

    setActiveTenant("tenant-1");
    Form form1 = database.save(getCurrentTenant().getId(), ImmutableForm.builder()
      .name("form1")
      .metadata(ImmutableFormMetadata.builder()
        .label("form1")
        .build()).build());

    String form2 = controlDatabase.createSnapshot(getCurrentTenant().getId(), form1.getId());

    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag1", null, form1.getId(), FormTag.Type.NORMAL, "user-1");
    controlDatabase.createTag(getCurrentTenant().getId(), "form1", "tag2", null, form2, FormTag.Type.NORMAL, "user-1");


    assertThrows(DocumentNotFoundException.class, () -> controlDatabase.moveTag(getCurrentTenant().getId(), ImmutableFormTag.builder()
      .formName("form1")
      .name("tag1")
      .refName("tag2")
      .type(FormTag.Type.MUTABLE)
      .build()));
  }

}
