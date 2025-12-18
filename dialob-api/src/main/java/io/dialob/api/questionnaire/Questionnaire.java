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
import io.dialob.api.annotation.Nullable;
import io.dialob.api.proto.ValueSet;
import io.dialob.api.validation.WithValidation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.util.*;

@Value.Builder
@Value.Style(deepImmutablesDetection = true, validationMethod = Value.Style.ValidationMethod.VALIDATION_API, jdkOnly = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
@JsonDeserialize(builder = Questionnaire.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
public record Questionnaire(
  @JsonProperty("_id")
  @Gson.Named("_id")
  @Id
  @Nullable
  @Getter
  String id,

  @JsonProperty("_rev")
  @Gson.Named("_rev")
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
) implements WithValidation<Questionnaire>, Serializable {

  public Questionnaire withId(@Nullable String id) {
    return new Builder().from(this).id(id).build();
  }

  public Questionnaire withRev(@Nullable String rev) {
    return new Builder().from(this).rev(rev).build();
  }

  public static class Builder extends QuestionnaireBuilder {


    @Override
    public Questionnaire build() {
      return super.build().validate();
    }
  }


  @Value.Builder
  @Value.Style(validationMethod = Value.Style.ValidationMethod.NONE, jdkOnly = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
  @JsonDeserialize(builder = Questionnaire.Metadata.Builder.class)
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
    Status status,

    @Nullable
    @Getter
    String formRev,

    @Nullable
    @Getter
    String tenantId,

    @Nullable
    @Getter
    Date created,

    @Nullable
    @Getter
    Date lastAnswer,

    @Nullable
    @Getter
    Date opened,

    @Nullable
    @Getter
    Date completed,

    @Nullable
    @Getter
    String label,

    @Nullable
    @Getter
    String submitUrl,

    /**
     * Completion reason, null if normally completed.
     */
    @Nullable
    @Getter
    Reason reason,

    @Nullable
    @Getter
    String language,

    /**
     * userId of document owner
     */
    @Nullable
    @Getter
    String owner,

    /**
     * userId of one who created questionnaire
     */
    @Nullable
    @Getter
    String creator,

    @JsonInclude
    @JsonAnyGetter
    @AllowNulls
    @Gson.Ignore
    @Getter
    Map<String, Object> additionalProperties
  ) implements Serializable {

    public Metadata {
      status = Objects.requireNonNullElse(status, Status.NEW);
    }

    public static class Builder extends MetadataBuilder {
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
