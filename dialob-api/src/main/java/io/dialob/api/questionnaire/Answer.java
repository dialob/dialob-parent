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
package io.dialob.api.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
import io.dialob.api.rest.HasId;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.time.Instant;

@Value.Builder
@JsonDeserialize(builder = Answer.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiType
public record Answer(
  @NonNull String id,

  /**
   * Text and number field answers stored in original format
   *
   * @return user's answer in original format
   */
  @JsonInclude(JsonInclude.Include.ALWAYS)
  @Getter @Nullable Object value,
  @Getter @Nullable String type,
  @Nullable Object acceptedValue,
  @Nullable Instant updated,
  @Nullable String userId
) implements HasId<String>, Serializable {

  public static class Builder extends AnswerBuilder { }

  public static Answer of(String id, @Nullable Object value) {
    return new Builder().id(id).value(value).build();
  }

  public static Answer copyOf(Answer answer) {
    return answer;
  }

}
