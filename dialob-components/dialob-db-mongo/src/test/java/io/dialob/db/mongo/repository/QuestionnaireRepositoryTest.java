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
package io.dialob.db.mongo.repository;

import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import io.dialob.api.questionnaire.*;
import io.dialob.db.mongo.AbsractMongoTest;
import io.dialob.db.mongo.convert.QuestionnaireReadingConverter;
import io.dialob.db.mongo.convert.QuestionnaireWritingConverter;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = QuestionnaireRepositoryTest.QuestionnaireRepositoryTestConfiguration.class)
public class QuestionnaireRepositoryTest extends AbsractMongoTest {

  public static final String MONGO_DATABASE = "questionnaires-test";

  @Rule
  public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb(MONGO_DATABASE);
  public static final Date CREATED = new Date(Instant.parse("2018-01-20T10:10:56.800Z").toEpochMilli());
  public static final Questionnaire IMMUTABLE_QUESTIONNAIRE = ImmutableQuestionnaire.builder()
    .id(null)
    .addAnswers(ImmutableAnswer.of("q1", "v1"))
    .addContext(ImmutableContextValue.of("c1", "c2"))
    .addVariableValues(ImmutableVariableValue.of("var1", 34))
    .addErrors(ImmutableError.of("err1", "CODE1", "Error 1"))
    .metadata(ImmutableQuestionnaireMetadata.builder().formId("123").created(CREATED).build())
    .build();


  @Configuration(proxyBeanMethods = false)
  @EnableMongoRepositories
  @ComponentScan(basePackageClasses = {QuestionnaireRepository.class})
  public static class QuestionnaireRepositoryTestConfiguration extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
      return MONGO_DATABASE;
    }

    @Override
    public MongoCustomConversions customConversions() {
      return new MongoCustomConversions(Arrays.asList(new QuestionnaireWritingConverter(), new QuestionnaireReadingConverter()));
    }

    @Override
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MongoClient mongoClient() {
      return createMongoClient(MongoClientSettings.builder().build());
    }
  }

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private QuestionnaireRepository repository;

  @Test
  @ShouldMatchDataSet()
  @Ignore // TODO Fix this
  @UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
  public void shouldInsertNewQuestionnaire() {
    Questionnaire questionnaire = repository.save(ModifiableQuestionnaire.create().from(IMMUTABLE_QUESTIONNAIRE));
    List<ModifiableQuestionnaire> questionnaires  = repository.findAll();
    assertNotNull(questionnaire.getId());
  }

  @Ignore // TODO MongoMappingContext should map interface types to implementation types
  @Test
  @UsingDataSet
  public void shouldReadQuestionnaire() {
    List<ModifiableQuestionnaire> questionnaires  = repository.findAll();
    assertEquals(IMMUTABLE_QUESTIONNAIRE, questionnaires.get(0).toImmutable().withId(null).withRev(null));
  }


  @Ignore // TODO MongoMappingContext should map interface types to implementation types
  @Test
  @UsingDataSet
  public void shouldDeleteQuestionnaire() {
    assertFalse(repository.findAll().isEmpty());
    repository.deleteById(repository.findAll().stream().findFirst().get().getId());
    assertTrue(repository.findAll().isEmpty());
  }


  @Ignore // TODO MongoMappingContext should map interface types to implementation types
  @Test
  @UsingDataSet
  public void shouldReadLegacyData() {
    List<ModifiableQuestionnaire> questionnaires  = repository.findAll();
    assertFalse(questionnaires.isEmpty());
  }
}
