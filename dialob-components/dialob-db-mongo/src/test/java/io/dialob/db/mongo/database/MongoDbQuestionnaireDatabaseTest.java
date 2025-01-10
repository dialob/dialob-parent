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
package io.dialob.db.mongo.database;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import io.dialob.api.form.ModifiableForm;
import io.dialob.api.questionnaire.ModifiableQuestionnaire;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.api.questionnaire.QuestionnaireFactory;
import io.dialob.db.mongo.MongoQuestionnaireIdObfuscator;
import io.dialob.db.mongo.repository.FormRepository;
import io.dialob.db.mongo.repository.QuestionnaireRepository;
import org.bson.codecs.configuration.CodecRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.DbCallback;
import org.springframework.data.mongodb.core.ExecutableFindOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@Import(MongoDbQuestionnaireDatabaseTest.TestConfiguration.class)
public class MongoDbQuestionnaireDatabaseTest {


  @Configuration(proxyBeanMethods = false)
  @EnableMongoRepositories(basePackageClasses = FormRepository.class)
  public static class TestConfiguration {

    @Bean
    public MongoMappingContext mongoMappingContext() {
      return new MongoMappingContext();
    }

    @Bean
    public DbRefResolver dbRefResolver() {
      return mock(DbRefResolver.class);
    }

    @Bean
    public MongoConverter mongoConverter() {
      return new MappingMongoConverter(dbRefResolver(),mongoMappingContext());
    }

    @Bean
    public MongoDatabase mongoDatabase() {
      return mock(MongoDatabase.class);
    }

    @Bean
    public CodecRegistry codecRegistry() {
      return mock(CodecRegistry.class);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
      MongoTemplate mongoTemplate = mock(MongoTemplate.class);
      when(mongoTemplate.getConverter()).thenReturn(mongoConverter());
      var executableFind = mock(ExecutableFindOperation.ExecutableFind.class);
      when(executableFind.as(any())).thenReturn(executableFind);
      var findWithProjection = mock(ExecutableFindOperation.FindWithProjection.class);
      when(findWithProjection.as(any())).thenReturn(findWithProjection);
      when(findWithProjection.matching(any(Query.class))).thenReturn(mock(ExecutableFindOperation.TerminatingFind.class));
      var terminatingFind = mock(ExecutableFindOperation.TerminatingFind.class);
      when(terminatingFind.all()).thenReturn(Collections.emptyList());
      when(executableFind.matching(any(Query.class))).thenReturn(terminatingFind);
      when(mongoTemplate.query(any())).thenReturn(executableFind);

      MongoDatabase mongoDatabase = mongoDatabase();
      CodecRegistry codecRegistry = codecRegistry();
      when(mongoDatabase.getCodecRegistry()).thenReturn(codecRegistry);

      when(mongoTemplate.execute(any(DbCallback.class))).thenAnswer(invocation -> {
        DbCallback dbCallback = invocation.getArgument(0);
        return dbCallback.doInDB(mongoDatabase);
      });
      return mongoTemplate;
    }

    @Bean
    public MongoQuestionnaireIdObfuscator mongoIdObfuscator() {
      return new MongoQuestionnaireIdObfuscator();
    }

    @Bean
    public MongoDbQuestionnaireDatabase database(QuestionnaireRepository repository, MongoQuestionnaireIdObfuscator mongoQuestionnaireIdObfuscator) {
      return new MongoDbQuestionnaireDatabase(repository, mongoQuestionnaireIdObfuscator);
    }
  }

  @Autowired
  private MongoDbQuestionnaireDatabase database;
  @Autowired
  private MongoTemplate mongoTemplate;
  @Autowired
  private MongoDatabase mongoDatabase;
  @Autowired
  private CodecRegistry codecRegistry;

  @AfterEach
  public void after() {
    if (codecRegistry != null) {
      Mockito.reset(codecRegistry);
    }
  }

  String tenantId = "123";

  @Test
  public void simpleTest() {
    assertNotNull(database);

    MongoConverter mongoConverter = mock(MongoConverter.class);

    when(mongoTemplate.getConverter()).thenReturn(mongoConverter);
    database.findAllMetadata(tenantId, null, null, null, null, null, metadataRow -> {});

    verify(mongoTemplate).query(ModifiableQuestionnaire.class);
    verify(mongoTemplate).query(ModifiableForm.class);
    verify(mongoTemplate, times(1)).setApplicationContext(any());
    verify(mongoTemplate, atLeast(0)).getConverter();
    verify(mongoTemplate, times(1)).execute(any(DbCallback.class));
    verify(mongoTemplate, times(2)).update(any(Class.class));
    verifyNoMoreInteractions(mongoTemplate, mongoDatabase, codecRegistry);
  }

  @Test
  public void shouldFindDocumentById() {
    assertNotNull(database);

    when(mongoTemplate.findOne(eq(Query.query(Criteria.where("id").is("1230"))),eq(ModifiableQuestionnaire.class),eq("modifiableQuestionnaire"))).thenReturn(ModifiableQuestionnaire.create().from(QuestionnaireFactory.questionnaire(null,"123")));

    assertNotNull(database.findOne(tenantId, "1230"));

    verify(mongoTemplate).findOne(eq(Query.query(Criteria.where("id").is("1230"))),eq(ModifiableQuestionnaire.class),eq("modifiableQuestionnaire"));
    verify(mongoTemplate, atLeast(0)).getConverter();
    verify(mongoTemplate).setApplicationContext(any(ApplicationContext.class));

    verify(mongoTemplate, times(2)).query(any(Class.class));
    verify(mongoTemplate, times(2)).update(any(Class.class));

    verifyNoMoreInteractions(mongoTemplate);
  }

  @Test
  public void shouldDeleteDocumentById() {
    assertNotNull(database);
    final DeleteResult deleteResult = Mockito.mock(DeleteResult.class);

    doReturn(deleteResult).when(mongoTemplate).remove(eq(Query.query(Criteria.where("id").is("1230"))),eq(ModifiableQuestionnaire.class),eq("modifiableQuestionnaire"));

    database.delete(tenantId, "1230");

    verify(mongoTemplate).remove(eq(Query.query(Criteria.where("id").is("1230"))),eq(ModifiableQuestionnaire.class),eq("modifiableQuestionnaire"));
    verify(mongoTemplate, atLeast(0)).getConverter();
    verify(mongoTemplate, atLeast(1)).findOne(eq(Query.query(Criteria.where("id").is("1230"))),eq(ModifiableQuestionnaire.class),eq("modifiableQuestionnaire"));
    verifyNoMoreInteractions(mongoTemplate, deleteResult);
  }


  @Test
  public void shouldConvertToMongoId() {
    QuestionnaireRepository repository = mock(QuestionnaireRepository.class);
    MongoQuestionnaireIdObfuscator idObfuscator = mock(MongoQuestionnaireIdObfuscator.class);
    MongoDbQuestionnaireDatabase database = new MongoDbQuestionnaireDatabase(repository, idObfuscator);

    Questionnaire q = QuestionnaireFactory.questionnaire(null, "123");
    Questionnaire q2 = database.toMongo(q);
    assertNotNull(q2);
    assertNull(q2.getId());
  }

  @Test
  public void blankIdEqualsToNewDocument() {
    QuestionnaireRepository repository = mock(QuestionnaireRepository.class);
    MongoQuestionnaireIdObfuscator idObfuscator = mock(MongoQuestionnaireIdObfuscator.class);
    MongoDbQuestionnaireDatabase database = new MongoDbQuestionnaireDatabase(repository, idObfuscator);

    Questionnaire q = QuestionnaireFactory.questionnaire("  ", "123");
    Questionnaire q2 = database.toMongo(q);
    assertNotNull(q2);
    assertNull(q2.getId());
  }

  @Test
  public void shouldTransformToMongoId() {
    QuestionnaireRepository repository = mock(QuestionnaireRepository.class);
    MongoQuestionnaireIdObfuscator idObfuscator = mock(MongoQuestionnaireIdObfuscator.class);
    MongoDbQuestionnaireDatabase database = new MongoDbQuestionnaireDatabase(repository, idObfuscator);

    Questionnaire q = QuestionnaireFactory.questionnaire("originalId","123");
    when(idObfuscator.toMongoId("originalId")).thenReturn("mongoId");

    Questionnaire q2 = database.toMongo(q);
    assertNotNull(q2);
    assertEquals("mongoId", q2.getId());
  }

  @Test
  public void cannotConvertInvalidId() {
    QuestionnaireRepository repository = mock(QuestionnaireRepository.class);
    MongoQuestionnaireIdObfuscator idObfuscator = mock(MongoQuestionnaireIdObfuscator.class);
    MongoDbQuestionnaireDatabase database = new MongoDbQuestionnaireDatabase(repository, idObfuscator);

    Questionnaire q = QuestionnaireFactory.questionnaire("fakeId","123");
    when(idObfuscator.toMongoId("fakeId")).thenReturn(null);

    Questionnaire q2 = database.toMongo(q);
    assertNull(q2);

    verify(idObfuscator).toMongoId("fakeId");
  }
}
