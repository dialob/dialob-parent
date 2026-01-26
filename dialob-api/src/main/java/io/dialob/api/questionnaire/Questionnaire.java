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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dialob.api.annotation.AllowNulls;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.rest.HasId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a questionnaire session, including its state, answers, and metadata.
 *
 * @param id The unique identifier of the questionnaire.
 * @param rev The revision of the questionnaire.
 * @param answers A list of answers provided in the questionnaire.
 * @param context A list of context values associated with the questionnaire.
 * @param activeItem The ID of the currently active item in the questionnaire.
 * @param errors A list of validation errors present in the questionnaire.
 * @param variableValues A list of variable values calculated during the session.
 * @param valueSets A list of value sets used in the questionnaire.
 * @param activeItems A set of IDs of items that are currently active (visible and enabled).
 * @param metadata Metadata associated with the questionnaire, such as status, timestamps, and ownership.
 */
@Value.Builder
@ApiType
@JsonDeserialize
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
public record Questionnaire(
  @JsonProperty("_id")
  @Id
  @Nullable
  String id,

  @JsonProperty("_rev")
  @Version
  @Nullable
  @Getter
  String rev,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<Answer> answers,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<ContextValue> context,

  @Nullable
  @Getter
  String activeItem,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<Error> errors,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<VariableValue> variableValues,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<ValueSet> valueSets,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  Set<String> activeItems,

  @Valid
  @NotNull
  @Getter
  Metadata metadata
) implements HasId<String>, Serializable {

  public Questionnaire withId(@Nullable String id) {
    return new Builder().from(this).id(id).build();
  }

  public Questionnaire withRev(@Nullable String rev) {
    return new Builder().from(this).rev(rev).build();
  }

  public static class Builder extends QuestionnaireBuilder {
  }


  /**
   * Metadata associated with a questionnaire.
   *
   * @param formId The ID of the form definition used for this questionnaire.
   * @param formName The name of the form.
   * @param status The current status of the questionnaire (e.g., NEW, OPEN, COMPLETED).
   * @param formRev The revision of the form definition.
   * @param tenantId The ID of the tenant that owns this questionnaire.
   * @param created The timestamp when the questionnaire was created.
   * @param lastAnswer The timestamp when the last answer was provided.
   * @param opened The timestamp when the questionnaire was last opened.
   * @param completed The timestamp when the questionnaire was completed.
   * @param label A label or description for the questionnaire.
   * @param submitUrl The URL to which the questionnaire should be submitted.
   * @param reason The reason for completion, if applicable (e.g., SKIPPED, CANCELLED).
   * @param language The language code for the questionnaire session.
   * @param owner The user ID of the document owner.
   * @param creator The user ID of the creator of the questionnaire.
   * @param additionalProperties Additional custom properties associated with the questionnaire.
   */
  @Value.Builder
  @ApiType
  @JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
  public record Metadata(

    @NotNull
    @Getter
    String formId,

    @Nullable
    @Getter
    String formName,

    @NotNull
    @Getter
    @Value.Default
    Status status,

    @Nullable
    @Getter
    String formRev,

    @Nullable
    @Getter
    String tenantId,

    @Nullable
    @Getter
    Instant created,

    @Nullable
    @Getter
    Instant lastAnswer,

    @Nullable
    @Getter
    Instant opened,

    @Nullable
    @Getter
    Instant completed,

    @Nullable
    @Getter
    String label,

    @Nullable
    @Getter
    String submitUrl,

    @Nullable
    @Getter
    Reason reason,

    @Nullable
    @Getter
    String language,

    @Nullable
    @Getter
    String owner,

    @Nullable
    @Getter
    String creator,

    @JsonInclude
    @JsonAnySetter
    @JsonAnyGetter
    @AllowNulls
    @Getter
    Map<String, Object> additionalProperties
  ) implements Serializable {

    public Metadata {
      status = Objects.requireNonNullElse(status, Status.NEW);
    }

    public static class Builder extends MetadataBuilder {

      public Builder() {
        super();
        status(Status.NEW);
      }

    }

    public enum Status {
      NEW,
      OPEN,
      COMPLETED
    }

    public enum Reason {
      SKIPPED,
      CANCELLED
    }

  }
}
