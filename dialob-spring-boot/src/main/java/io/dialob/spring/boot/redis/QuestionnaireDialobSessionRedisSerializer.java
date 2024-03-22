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
package io.dialob.spring.boot.redis;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import io.dialob.api.questionnaire.Questionnaire;
import io.dialob.questionnaire.service.api.event.QuestionnaireEventPublisher;
import io.dialob.questionnaire.service.api.session.QuestionnaireSessionService;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.sp.AsyncFunctionInvoker;
import io.dialob.session.engine.sp.DialobQuestionnaireSession;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.noop.NoopTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class QuestionnaireDialobSessionRedisSerializer implements RedisSerializer<DialobQuestionnaireSession> {

  private final QuestionnaireSessionService questionnaireSessionService;

  private final QuestionnaireEventPublisher eventPublisher;

  private final DialobProgramService dialobProgramService;

  private final DialobSessionEvalContextFactory sessionContextFactory;

  private final AsyncFunctionInvoker asyncFunctionInvoker;

  private final Timer serializationTimer;

  private final Timer deserializationTimer;

  private final int bufferSize;

  public QuestionnaireDialobSessionRedisSerializer(@NonNull QuestionnaireSessionService questionnaireSessionService,
                                                   QuestionnaireEventPublisher eventPublisher,
                                                   DialobProgramService dialobProgramService,
                                                   DialobSessionEvalContextFactory sessionContextFactory,
                                                   AsyncFunctionInvoker asyncFunctionInvoker,
                                                   @NonNull Optional<MeterRegistry> meterRegistry,
                                                   int bufferSize) {
    this.questionnaireSessionService = questionnaireSessionService;
    this.eventPublisher = eventPublisher;
    this.dialobProgramService = dialobProgramService;
    this.sessionContextFactory = sessionContextFactory;
    this.asyncFunctionInvoker = asyncFunctionInvoker;
    final NoopTimer noopTimer = new NoopTimer(null);
    this.serializationTimer = meterRegistry.map(registry -> Timer.builder("session.serialization.time").register(registry)).orElse(noopTimer);
    this.deserializationTimer = meterRegistry.map(registry -> Timer.builder("session.deserialization.time").register(registry)).orElse(noopTimer);
    this.bufferSize = bufferSize;
  }

  @Override
  public byte[] serialize(@Nullable DialobQuestionnaireSession dialobQuestionnaireSession) {
    if (dialobQuestionnaireSession == null) {
      return null;
    }
    return serializationTimer.record(() -> {
      final ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
      CodedOutputStream output = CodedOutputStream.newInstance(byteBuffer);
      try {
        dialobQuestionnaireSession.writeTo(output);
        output.flush();
      } catch (IOException e) {
        throw new SerializationException("ProtoBuf serialization failed. Session " + dialobQuestionnaireSession.getSessionId(), e);
      }
      final int totalBytesWritten = output.getTotalBytesWritten();
      LOGGER.trace("serialized {} to redis into {} bytes", dialobQuestionnaireSession.getSessionId(), totalBytesWritten);
      return Arrays.copyOf(byteBuffer.array(), totalBytesWritten);
    });
  }

  @Override
  public DialobQuestionnaireSession deserialize(@Nullable byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    LOGGER.trace("deserialize from redis {} bytes", bytes.length);
    return deserializationTimer.record(() -> {
      CodedInputStream input = CodedInputStream.newInstance(bytes);
      try {
        return restoreSessionFrom(input);
      } catch (IOException e) {
        throw new SerializationException("ProtoBuf deserialization failed", e);
      }
    });
  }

  @NonNull
  protected DialobQuestionnaireSession restoreSessionFrom(@NonNull CodedInputStream input) throws IOException {
    DialobQuestionnaireSession.Builder builder = DialobQuestionnaireSession.builder()
      .eventPublisher(eventPublisher)
      .sessionContextFactory(sessionContextFactory)
      .asyncFunctionInvoker(asyncFunctionInvoker)
      .readFrom(input);
    Questionnaire.Metadata metadata = builder.getMetadata();
    return builder
      .dialobProgram(dialobProgramService.findByFormIdAndRev(metadata.getFormId(), metadata.getFormRev()))
      .build();
  }

}
