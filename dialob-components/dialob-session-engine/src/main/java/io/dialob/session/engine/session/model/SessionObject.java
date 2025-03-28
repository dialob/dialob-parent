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

import java.io.Serializable;

public interface SessionObject extends Serializable {

  <I extends ItemId> I getId();

  /**
   * @return true when item is relevant to questionnaire.
   */
  default boolean isActive() {
    return true;
  }

  /**
   * @return true if item should not be shown to user
   */
  default boolean isDisabled() {
    return false;
  }

  /**
   * @return true if item is not variable or something similar that will not be shown to user.
   */
  default boolean isDisplayItem() {
    return false;
  }

}
