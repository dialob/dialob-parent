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
package io.dialob.security.user;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class CurrentUser implements Serializable {

  private final String userId;

  private final String displayName;

  private final String firstName;

  private final String lastName;

  private final String email;

  public CurrentUser(String userId, String displayName, String firstName, String lastName, String email) {
    this.userId = userId;
    this.displayName = displayName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

}
