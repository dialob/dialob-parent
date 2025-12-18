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
package io.dialob.api.proto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
import io.dialob.api.questionnaire.Error;
import io.dialob.api.rest.HasId;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;

@Value.Builder
@JsonDeserialize(builder = Action.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@ApiType
public record Action(
  @Getter
  Type type,

  @Nullable
  String id,

  @Nullable
  @Getter
  String message,

  @Nullable
  @Getter
  String trace,

  @Nullable
  @Getter
  ActionItem item,

  @Nullable
  @Getter
  Error error,

  @Nullable
  @Getter
  Object answer,

  @Nullable
  @Getter
  List<String> ids,

  @Nullable
  @Getter
  Object value,

  @Nullable
  @Getter
  ValueSet valueSet,

  @JsonIgnore
  @Nullable
  @Getter
  Boolean serverEvent,

  @JsonIgnore
  @Nullable
  @Getter
  String resourceId
) implements HasId<String>, Serializable {

  public static class Builder extends ActionBuilder {
  }

  public enum Type {
    ANSWER(true),
    NEXT(true),
    PREVIOUS(true),
    GOTO(true),
    COMPLETE(true),
    ADD_ROW(true),
    DELETE_ROW(true),
    REMOVE_ANSWERS(true),
    RESET(false),
    ITEM(false),
    REMOVE_ITEMS(false),
    ERROR(false),
    REMOVE_ERROR(false),
    VALUE_SET(false),
    REMOVE_VALUE_SETS(false),
    NOT_FOUND(false),
    SERVER_ERROR(false),
    ROWS(false),
    SET_VALUE(false),
    SET_FAILED(false),
    LOCALE(false),
    SET_LOCALE(true);

    @Getter
    final boolean clientAction;

    Type(boolean clientAction) {
      this.clientAction = clientAction;
    }

  }

}
