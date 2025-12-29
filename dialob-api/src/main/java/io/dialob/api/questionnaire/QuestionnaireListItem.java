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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.rest.HasId;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;

@Value.Builder
@JsonDeserialize(builder = QuestionnaireListItem.Builder.class)
@ApiType
public record QuestionnaireListItem(
  @NonNull String id,
  @Getter Questionnaire.Metadata metadata
) implements HasId<String>, Serializable {

  public static class Builder extends QuestionnaireListItemBuilder { }

  public static QuestionnaireListItem copyOf(QuestionnaireListItem questionnaireListItem) {
    return questionnaireListItem;
  }


}
