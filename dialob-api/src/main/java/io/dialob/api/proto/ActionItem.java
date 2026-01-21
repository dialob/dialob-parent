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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.dialob.api.annotation.AllowNulls;
import io.dialob.api.annotation.ApiType;
import io.dialob.api.annotation.Nullable;
import io.dialob.api.rest.HasId;
import lombok.Getter;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ActionItem represents a single item in a form, such as a question, a group, or a page.
 *
 * @param id unique identifier of the item.
 * @param type type of the item.
 * @param view
 * @param label label to display
 * @param description description to display
 * @param inactive
 * @param disabled
 * @param required
 * @param className
 * @param value value to display. When item is question this is {@link io.dialob.session.engine.session.model.ItemState#answer()}
 *             and for the variables this is {@link io.dialob.session.engine.session.model.ItemState#value()}.
 * @param items list of subitems in group
 * @param activeItem current active item in group eg. current page.
 * @param availableItems
 * @param allowedActions active actions for item. eg. page navigation or row addition/removal.
 * @param answered
 * @param valueSetId
 */
@Value.Builder
@JsonDeserialize(builder = ActionItem.Builder.class)
@JsonInclude(content = JsonInclude.Include.NON_NULL, value = JsonInclude.Include.NON_EMPTY)
@ApiType
public record ActionItem(

  String id,

  @Getter
  String type,

  @Nullable @Getter String view,

  @Nullable @Getter String label,

  @Nullable @Getter String description,

  @Nullable @Getter Boolean inactive,

  @Nullable @Getter Boolean disabled,

  @Nullable @Getter Boolean required,

  @Nullable @Getter Boolean readOnly,

  @Nullable @Getter List<String> className,

  @Nullable @Getter Object value,

  @Nullable @Getter List<String> items,

  @Nullable @Getter String activeItem,

  @Nullable @Getter List<String> availableItems,

  @Nullable @Getter Set<Action.Type> allowedActions,

  @Nullable @Getter Boolean answered,

  @Nullable @Getter String valueSetId,

  @Nullable @AllowNulls
  @Getter
  Map<String, Object> props
) implements HasId<String>, Serializable {

  public static class Builder extends ActionItemBuilder {
  }


}
