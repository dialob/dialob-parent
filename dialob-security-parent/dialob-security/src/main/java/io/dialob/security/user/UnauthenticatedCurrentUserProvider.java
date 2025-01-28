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

import edu.umd.cs.findbugs.annotations.NonNull;

public class UnauthenticatedCurrentUserProvider implements CurrentUserProvider {

  public static final String UNAUTHENTICATED = "unauthenticated";

  public static final CurrentUser UNAUTHENTICATED_USER = new CurrentUser(UNAUTHENTICATED, UNAUTHENTICATED, null, null, null);

  public static final CurrentUserProvider INSTANCE = new UnauthenticatedCurrentUserProvider();

  private UnauthenticatedCurrentUserProvider() {
  }

  @NonNull
  @Override
  public CurrentUser get() {
    return UNAUTHENTICATED_USER;
  }
}
