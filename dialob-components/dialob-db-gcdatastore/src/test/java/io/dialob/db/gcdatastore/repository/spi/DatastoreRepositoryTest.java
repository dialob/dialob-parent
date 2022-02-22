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
package io.dialob.db.gcdatastore.repository.spi;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import io.dialob.api.form.Form;
import io.dialob.api.form.ImmutableForm;
import io.dialob.api.form.ImmutableFormItem;
import io.dialob.api.form.ImmutableFormMetadata;
import org.junit.jupiter.api.*;
import org.threeten.bp.Duration;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatastoreRepositoryTest {

  static LocalDatastoreHelper datastoreHelper = LocalDatastoreHelper.create();
  ObjectMapper mapper = new ObjectMapper().registerModules(new JavaTimeModule());

  @BeforeAll
  public static void setUpClass() throws Exception, InterruptedException {
    datastoreHelper.start();
  }

  @AfterAll
  public static void tearDownClass() throws Exception {
    Duration timeout = Duration.ofSeconds(5);
    datastoreHelper.stop(timeout);
  }

  @BeforeEach
  public void setUp() throws Exception{
    datastoreHelper.reset();
  }

  @Test
  public void testSaveRead() {
    Form document = initForm(ImmutableForm.builder()).build();
    DatastoreFormRepository repo = getRepo();
    Form savedDocument = repo.save(document);
    Form foundDocument = repo.findOne(savedDocument.getId());
    assertEquals(savedDocument, foundDocument);
  }

  @Test
  public void testVersionSave() {
    Form document = initForm(ImmutableForm.builder()).build();
    DatastoreFormRepository repo = getRepo();
    Form savedDocument = repo.save(copy(document));
    Form savedDocument2 = repo.save(copy(savedDocument));

    assertEquals(Integer.valueOf(savedDocument.getRev())+1, Integer.valueOf(savedDocument2.getRev()).intValue());
  }

  @Test
  public void testConcurrentSave() {
    Form document = initForm(ImmutableForm.builder()).build();
    DatastoreFormRepository repo = getRepo();
    Form savedDocument = repo.save(copy(document));
    Form savedDocument2 = repo.save(copy(savedDocument));
    try {
      repo.save(copy(savedDocument));
      Assertions.fail("Should fail, old version saving");
    }
    catch (ConcurrentModificationException e) {
      // expected
    }
    Form savedDocument3 = repo.save(savedDocument2);
    assertEquals("3", savedDocument3.getRev());
  }

  // copy method to avoid modification of passed to save object
  Form copy(Form form) {

    try {
      String valueAsString = mapper.writeValueAsString(form);
      return mapper.readValue(valueAsString, Form.class);
    } catch (IOException e) {
      Assertions.fail("Should not reach");
    }
    return null;
  }
  @Test
  public void testConcurrentSaveOverwriting() {
    Form document = initForm(ImmutableForm.builder()).build();
    DatastoreFormRepository repo = getRepo();
    Form savedDocument = repo.save(copy(document));
    Form savedDocument2 = repo.save(copy(savedDocument));
    try {
      Form copy = copy(savedDocument);
      repo.save(copy);
    }
    catch (ConcurrentModificationException e) {
      for (int i=0; i<10; i++) {
        try {
          Form copy = copy(savedDocument);
          repo.save(copy);
          Assertions.fail("Should fail, old version saving");
        }
        catch (ConcurrentModificationException e1) {
          // retry it
        }
      }
    }
    Form savedDocument3 = repo.save(copy(savedDocument2));
    assertEquals("3", savedDocument3.getRev());
  }

  private DatastoreFormRepository getRepo() {
    Datastore datastore = datastoreHelper.getOptions().getService();
    return new DatastoreFormRepository(datastore, mapper, null, "forms");
  }

  private ImmutableForm.Builder initForm(ImmutableForm.Builder builder) {
    return builder.metadata(ImmutableFormMetadata.builder()
      .created(new Date())
      .creator("Tester")
      .defaultSubmitUrl("http://localhost:9080/")
      .label("Label")
      .tenantId("tenant_id")
      .build())
      .putData("questionnaire", ImmutableFormItem.builder().id("item").type("itemtype").putLabel("en", "dialob dialog").build());
  }

}
