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
package io.dialob.session.engine.session.model;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import io.dialob.session.engine.Utils;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ErrorStateTest {

  @Test
  void shouldEquals() {
    EqualsVerifier.forClass(ErrorState.class)
      .suppress(Warning.NONFINAL_FIELDS)
      .verify();
  }

  @Test
  void testUtils() {
    ErrorState errorState = new ErrorState(IdUtils.toId("id"), "code", "message");

    var error = Utils.toError(errorState);
    org.junit.jupiter.api.Assertions.assertNotNull(error);
    org.junit.jupiter.api.Assertions.assertEquals("message", error.getDescription());
    org.junit.jupiter.api.Assertions.assertEquals("code", error.getCode());
    org.junit.jupiter.api.Assertions.assertEquals("id", error.getId());
  }

  @Test
  void shouldNotCreateNewStateWhenNoChanges() {
    ErrorState errorState = new ErrorState(IdUtils.toId("id"), "code", "message");
    ErrorState updated = errorState
      .update(null)
      .setActive(false)
      .get();
    Assertions.assertThat(updated).isSameAs(errorState);
    updated = errorState
      .update(null)
      .setActive(true)
      .get();
    Assertions.assertThat(updated).isNotSameAs(errorState);
  }

  static Stream<ErrorState> errorStates() {
    return Stream.of(
      new ErrorState(IdUtils.toId("id"), "code", "message"),
      new ErrorState(IdUtils.toId("id"), null, "message")
    );
  }

  @ParameterizedTest
  @MethodSource("errorStates")
  void shouldSerialize(ErrorState errorState) throws Exception {
    byte[] bytes;
    try (var output = new java.io.ByteArrayOutputStream()) {
      CodedOutputStream output1 = CodedOutputStream.newInstance(output);
      errorState.writeTo(output1);
      output1.flush();
      bytes = output.toByteArray();
    }
    ErrorState parsed = ErrorState.readFrom(CodedInputStream.newInstance(bytes));
    Assertions.assertThat(parsed).isEqualTo(errorState);
  }

}
