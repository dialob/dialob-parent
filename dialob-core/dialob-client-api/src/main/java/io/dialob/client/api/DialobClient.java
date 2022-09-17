package io.dialob.client.api;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormTag;
import io.dialob.api.form.FormValidationError;
import io.dialob.api.proto.Actions;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.questionnaire.Answer;
import io.dialob.api.questionnaire.ContextValue;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.client.api.DialobStore.BodySource;
import io.dialob.client.api.DialobStore.BodyType;
import io.dialob.client.api.DialobStore.StoreEntity;
import io.dialob.program.DialobProgram;
import io.smallrye.mutiny.Uni;

public interface DialobClient {
  
  //origin-dialob-session-boot all tests
  
  BodySourceBuilder ast();
  ProgramBuilder program();
  QuestionnaireExecutorBuilder executor(ProgramEnvir envir);   
  EnvirBuilder envir();

  DialobClientConfig config();
  DialobStore store();
  RepoBuilder repo();

  interface QuestionnaireExecutorBuilder {
    QuestionnaireExecutor create(String id, String rev, Consumer<QuestionnaireInit> initWith);
    QuestionnaireExecutor create(String id, Consumer<QuestionnaireInit> initWith);
    QuestionnaireExecutor restore(Questionnaire queestionnaire);
  }
  
  interface QuestionnaireInit {
    QuestionnaireInit activeItem(String activeItem); // TODO:: not needed
    QuestionnaireInit status(Questionnaire.Metadata.Status status); // TODO:: always new, NEW - NEW DOC, user not touched, OPEN - user has started
    QuestionnaireInit valueSets(List<ValueSet> valueSets); // TODO:: not needed
    QuestionnaireInit questionnaire(Questionnaire questionnaire); // TODO:: required why?
    QuestionnaireInit submitUrl(String submitUrl); //TODO:: classifier on submit/ not really needed
    
    QuestionnaireInit id(String id);
    QuestionnaireInit rev(String rev);
    QuestionnaireInit creator(String owner);
    QuestionnaireInit owner(String owner);
    QuestionnaireInit language(String language); //Optional
    QuestionnaireInit additionalProperties(Map<String, Object> additionalProperties); //Optional
    QuestionnaireInit contextValues(List<ContextValue> contextValues); //Optional
    QuestionnaireInit answers(List<Answer> answers); //Optional
  }
  
  interface QuestionnaireExecutor {
    // TODO:: only in tests false, everywhere else true???
    QuestionnaireExecutor createOnly(boolean createOnly);
    QuestionnaireExecutor actions(Actions actions);
    Actions execute();
    QuestionnaireSession toSession();
  }
  
  interface ProgramBuilder {
    ProgramBuilder form(Form form);
    DialobProgram build();
  }
  
  interface BodySourceBuilder {
    BodySourceBuilder id(String id);
    BodySourceBuilder syntax(String src);
    BodySourceBuilder syntax(InputStream syntax);
    BodySource build(BodyType type);
  }
  
  interface EnvirBuilder {
    EnvirBuilder from(ProgramEnvir envir);
    EnvirCommandFormatBuilder addCommand();
    ProgramEnvir build();
  }

  
  interface EnvirCommandFormatBuilder {
    EnvirCommandFormatBuilder id(String externalId);
    EnvirCommandFormatBuilder cachless();

    EnvirCommandFormatBuilder tag(String json);
    EnvirCommandFormatBuilder form(String json);
    EnvirCommandFormatBuilder form(InputStream json);
    
    EnvirCommandFormatBuilder tag(StoreEntity entity);
    EnvirCommandFormatBuilder form(StoreEntity entity);
    
    EnvirBuilder build();
  }

  interface RepoBuilder {
    RepoBuilder repoName(String repoName);
    RepoBuilder headName(String headName);
    Uni<DialobClient> create();
    DialobClient build();
  }
  
  
  interface ProgramEnvir {
    ProgramWrapper findByFormId(String formId);
    ProgramWrapper findByFormIdAndRev(String formId, String formRev);
    List<ProgramWrapper> findAll();
  }
  
  @Value.Immutable
  interface ProgramWrapper {
    String getId();
    ProgramStatus getStatus();
    
    List<ProgramMessage> getErrors();

    BodySource getSource();
    @JsonIgnore
    Optional<Form> getAst();
    @JsonIgnore
    Optional<DialobProgram> getProgram();
  } 
  
  @Value.Immutable
  interface ProgramMessage {
    String getId();
    
    @Nullable
    String getMsg();
    
    @Nullable
    FormValidationError getSrc();
    @JsonIgnore
    @Nullable
    Exception getException();
  }
  

  enum ProgramStatus { 
    UP, 
    AST_ERROR, 
    PROGRAM_ERROR, 
    DEPENDENCY_ERROR }

  interface TypesMapper {
    Map<String, Serializable> toMap(Object entity);
    Map<String, Serializable> toMap(JsonNode entity);

    String toString(InputStream entity);
    
    
    Questionnaire toQuestionnaire(InputStream entity);
    Form toForm(InputStream entity);
    Form toForm(String entity);
    FormTag toFormTag(String entity);
    
    Object toType(Object value, Class<?> toType);
    String toJson(Object anyObject);
  }
  
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface ServiceData {
    String value() default "";
  }
}
