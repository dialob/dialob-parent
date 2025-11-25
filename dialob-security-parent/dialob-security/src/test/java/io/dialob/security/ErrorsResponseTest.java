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
package io.dialob.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.RegularExpressionValueMatcher;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.time.Instant;

class ErrorsResponseTest {

  public static final RegularExpressionValueMatcher<Object> TIMESTAMP_MATCHER = new RegularExpressionValueMatcher<>("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(.\\d{1,6})?Z");
  ObjectMapper om = new ObjectMapper();

  @Test
  void readEmptyResponse() throws JSONException {
    var error = ErrorsResponse.builder()
      .build();
    String actual = om.writeValueAsString(error);
    JSONAssert.assertEquals(
      """
        {
          "timestamp" : "..."
        }""", actual, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp",
          TIMESTAMP_MATCHER)));
  }

  @Test
  void shouldSetTimestampIfNull() {
    ErrorsResponse response = new ErrorsResponse(null, 400, "error", "message");
    assertNotNull(response.timestamp());
  void readWithFields() throws JSONException {
    var error = ErrorsResponse.builder()
      .message("Message")
      .error("Error")
      .build();
    String actual = om.writeValueAsString(error);
    JSONAssert.assertEquals(
      """
        {
          "error": "Error",
          "message": "Message",
          "timestamp" : "..."
        }""", actual, new CustomComparator(JSONCompareMode.STRICT,
        new Customization("timestamp",
          TIMESTAMP_MATCHER)));
  }


  @Test
  void shouldSerializeAsJson() throws JsonProcessingException {
    var om = new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    var json = om.writeValueAsString(new ErrorsResponse.Builder().build());
    JsonAssertions.assertThatJson(json)
      .isObject()
      .doesNotContainKeys("status", "error", "message")
      .node("timestamp").isString().matches("\\d{4}-[0-1]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d(\\.\\d+)?(Z|[+-]\\d{2}(:\\d{2})?)?");
  void writeWithEmptyFields() {
    var errorsResponse = om.readValue("""
      {
        "timestamp": "2025-01-01T12:00:00.001Z"
      }""", ErrorsResponse.class);
    Assertions.assertNull(errorsResponse.message());
    Assertions.assertNull(errorsResponse.error());
    Assertions.assertEquals(Instant.parse("2025-01-01T12:00:00.001Z"), errorsResponse.timestamp());
    Assertions.assertNull(errorsResponse.status());
  }

  @Test
  void writeWithFields() {
    var errorsResponse = om.readValue("""
      {
        "error": "Error",
        "message": "Message",
        "status": 400,
        "timestamp": "2025-01-01T12:10:00Z"
      }""", ErrorsResponse.class);
    Assertions.assertEquals("Message", errorsResponse.message());
    Assertions.assertEquals("Error", errorsResponse.error());
    Assertions.assertEquals(Instant.parse("2025-01-01T12:10:00Z"), errorsResponse.timestamp());
    Assertions.assertEquals(400, errorsResponse.status());
  }


}
