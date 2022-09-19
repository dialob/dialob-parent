package io.dialob.client.tests.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;

import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.dialob.api.form.Form;
import io.dialob.api.questionnaire.ImmutableQuestionnaire;
import io.dialob.api.questionnaire.ImmutableQuestionnaireMetadata;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.client.api.DialobCache;
import io.dialob.client.api.DialobClientConfig;
import io.dialob.client.api.DialobStore;
import io.dialob.client.api.ImmutableDialobClientConfig;
import io.dialob.client.spi.DialobClientImpl;
import io.dialob.client.spi.DialobEhCache;
import io.dialob.client.spi.DialobTypesMapperImpl;
import io.dialob.client.spi.event.QuestionnaireEventPublisher;
import io.dialob.client.spi.function.AsyncFunctionInvoker;
import io.dialob.compiler.DialobProgramFromFormCompiler;
import io.dialob.program.DialobSessionEvalContextFactory;
import io.dialob.program.EvalContext;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.function.DefaultFunctions;
import io.dialob.rule.parser.function.FunctionRegistry;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

public class DialobClientTest extends DialobClientImpl {

  private static DialobClientTest INSTANCE = DialobClientTest.builder().build();
  
  public DialobClientTest(DialobClientConfig config) {
    super(config);
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public DialobClientTest with(QuestionnaireEventPublisher publisher) {
    final var newConfig = ImmutableDialobClientConfig.builder().from(this.config()).eventPublisher(publisher).build();
    return new DialobClientTest(newConfig);
  }
  
  public static DialobClientTest get() {
    return INSTANCE;
  }
  
  @Accessors(fluent = true, chain = true)
  @Data
  @EqualsAndHashCode(callSuper = false)
  public static class Builder extends DialobClientImpl.Builder {  

    
    public DialobClientTest build() {
      DialobCache cache = this.cache();
      if(cache == null) {
        cache = DialobEhCache.builder().build("inmem");
      }
      
      Clock clock = this.clock();
      if(clock == null) {
        clock = Clock.systemDefaultZone();
      }

      ObjectMapper objectMapper = this.objectMapper();
      if(objectMapper == null) {
        objectMapper = new ObjectMapper().registerModules(new JavaTimeModule(), new Jdk8Module());
      }
      
      QuestionnaireEventPublisher eventPublisher = this.eventPublisher();
      if(eventPublisher == null) {
        eventPublisher = Mockito.mock(QuestionnaireEventPublisher.class);
      }
      
      DialobStore store = this.store();
      if(store == null) {
        store = Mockito.mock(DialobStore.class);
      }
      
      final var visitor = mock(EvalContext.UpdatedItemsVisitor.AsyncFunctionCallVisitor.class);
      final var asyncFunctionInvoker = mock(AsyncFunctionInvoker.class);
      when(asyncFunctionInvoker.createVisitor(Mockito.any())).thenReturn(visitor);
      final var functionRegistry = createFunctionRegistry();
      
      
      final var config = ImmutableDialobClientConfig.builder()
          .asyncFunctionInvoker(asyncFunctionInvoker)
          .factory(new DialobSessionEvalContextFactory(functionRegistry, clock, dialobSessionUpdateHook()))
          .store(store)
          .cache(cache)
          .eventPublisher(eventPublisher)
          .mapper(new DialobTypesMapperImpl(objectMapper))
          .compiler(new DialobProgramFromFormCompiler(functionRegistry))
          .build();
      

      return new DialobClientTest(config);
    }
  }

  
  private static FunctionRegistry createFunctionRegistry() {
    try {
      final FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
  
      when(functionRegistry.returnTypeOf("today")).thenReturn(ValueType.DATE);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(LocalDate.now());
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("today"));
  
      when(functionRegistry.returnTypeOf(eq("isLyt"),eq(ValueType.STRING))).thenReturn(ValueType.BOOLEAN);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(DefaultFunctions.isLyt(invocationOnMock.getArgument(1)));
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("isLyt"));
  
      when(functionRegistry.returnTypeOf(eq("isHetu"),eq(ValueType.STRING))).thenReturn(ValueType.BOOLEAN);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(DefaultFunctions.isHetu(invocationOnMock.getArgument(1)));
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("isHetu"));
  
      when(functionRegistry.returnTypeOf(eq("count"),eq(ValueType.arrayOf(ValueType.INTEGER)))).thenReturn(ValueType.INTEGER);
      doAnswer(invocationOnMock -> {
        ((FunctionRegistry.FunctionCallback) invocationOnMock.getArgument(0)).succeeded(DefaultFunctions.count(invocationOnMock.getArgument(1)));
        return null;
      }).when(functionRegistry).invokeFunction(any(FunctionRegistry.FunctionCallback.class), eq("count"));
  
  
      when(functionRegistry.returnTypeOf("lengthOf", ValueType.STRING)).thenReturn(ValueType.INTEGER);
      doAnswer(invocation -> {
        FunctionRegistry.FunctionCallback cb = invocation.getArgument(0);
        String string = invocation.getArgument(2);
        if (string == null) {
          cb.succeeded(0);
        } else {
          cb.succeeded(string.length());
        }
        return null;
      }).when(functionRegistry).invokeFunction(any(), eq("lengthOf"), any());
  
      when(functionRegistry.returnTypeOf("count", ValueType.arrayOf(ValueType.STRING))).thenReturn(ValueType.INTEGER);
      return functionRegistry;
    } catch(Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
  
  
  public static FillAssertionBuilder fillForm(String formFile) {
    final var client = get();
    final var envir = client.envir()
        .addCommand()
          .id(formFile)
          .form(Thread.currentThread().getContextClassLoader().getResourceAsStream(formFile)).build()
        .build();
    final var formId = envir.findAll().stream().findFirst().get().getAst().get().getValue().getId();
    return new FillAssertionBuilder(formId, client, envir);
  }

  public static FillAssertionBuilder fillForm(String formFile, String questionnaireState) throws java.io.IOException {
    final var client = get();
    final var formDocument = client.config().getMapper().toForm(Thread.currentThread().getContextClassLoader().getResourceAsStream(formFile));
    final var questionnaire = client.config().getMapper().toQuestionnaire(Thread.currentThread().getContextClassLoader().getResourceAsStream(questionnaireState));
    return fillForm(formDocument.getValue(), questionnaire);
  }

  public static FillAssertionBuilder fillForm(Form formDocument, Questionnaire questionnaire) throws java.io.IOException {
    String docId = UUID.randomUUID().toString();
    String formId = formDocument.getId();
    questionnaire = ImmutableQuestionnaire.builder().from(questionnaire).id(docId)
        .metadata(ImmutableQuestionnaireMetadata.builder().from(questionnaire.getMetadata()).formId(formId).build()).build();

    final var client = get();
    final var envir = client.envir()
        .addCommand().id(formId).form(client.config().getMapper().toJson(formDocument)).build()
        .build();
    return new FillAssertionBuilder(questionnaire, client, envir);
  }


}
