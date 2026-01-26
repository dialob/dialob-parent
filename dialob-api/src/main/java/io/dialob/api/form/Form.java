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
package io.dialob.api.form;

import com.fasterxml.jackson.annotation.*;
import io.dialob.api.annotation.AllowNulls;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
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
import java.util.Set;

/**
 * Represents a form object that holds a collection of information required for form management
 * in the system. This class provides a structure to define form-specific metadata, data, variables,
 * namespaces, value sets, and error messages related to required fields.
 * <p>
 * This interface makes use of immutability and supports serialization/deserialization
 * using libraries such as JSON and Gson. It also integrates with MongoDB for repository storage.
 * <p>
 * The Form interface supports validation rules and ensures non-null constraints for key fields.
 *
 * @param id The form's unique identifier as a String, or null if the identifier is not set.
 * @param rev The revision identifier as a String, or null if the revision is not set. The revision identifier
 *            is used to track changes to the form, ensuring version control and
 *            consistency during updates or modifications.
 * @param requiredErrorText  Error text for required fields, unless not defined per item.
 *
 */
@Value.Builder
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"saving","rules","updated","failed", "serviceCalls"})
@ApiType
public record Form(
  @JsonProperty("_id")
  @Id
  @Nullable
  String id,

  @JsonProperty("_rev")
  @Version
  @Nullable
  @Getter
  String rev,

  @Nullable
  @Getter
  String name,

  @Valid
  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  Map<String, FormItem> data,

  @Valid
  @NotNull
  @Getter
  Metadata metadata,

  @Valid
  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<Variable> variables,

  @Valid
  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  Map<String, Form> namespaces,

  @Valid
  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  List<FormValueSet> valueSets,

  @NotNull
  @JsonSetter(nulls = Nulls.AS_EMPTY)
  @Getter
  Map<String, String> requiredErrorText

) implements HasId<String>, FormEntity {

  public Form withRev(String number) {
    return new Form.Builder().from(this).rev(number).build();
  }

  public static class Builder extends FormBuilder {
  }


  @Value.Builder
  @ApiType
  @JsonIgnoreProperties(ignoreUnknown = true)
  @JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
  public record Metadata(
    @NotNull
    @Getter
    String label,

    @Getter
    @Nullable Instant created,

    @Getter
    @Nullable Instant lastSaved,

    @Getter
    @Nullable Boolean valid,

    @Getter
    @Nullable String creator,

    @Getter
    @Nullable String tenantId,

    @Getter
    @Nullable String savedBy,

    @NotNull
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Getter
    Set<String> labels,

    @Getter
    @Nullable String defaultSubmitUrl,

    @NotNull
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Getter
    Set<String> languages,

    @JsonInclude
    @JsonAnyGetter
    @JsonAnySetter
    @AllowNulls
    @Getter
    Map<String, Object> additionalProperties

  ) implements Serializable {

    @JsonIgnoreType
    public static class Builder extends MetadataBuilder {
    }

  }
}
