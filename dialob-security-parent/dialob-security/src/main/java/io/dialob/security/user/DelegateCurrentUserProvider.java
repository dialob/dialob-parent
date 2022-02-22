/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class DelegateCurrentUserProvider implements CurrentUserProvider {

  private final List<CurrentUserProvider> currentUserProviders;

  public DelegateCurrentUserProvider(CurrentUserProvider ...currentUserProviders) {
    this(Arrays.asList(currentUserProviders));
  }


  public DelegateCurrentUserProvider(Collection<CurrentUserProvider> currentUserProviders) {
    this.currentUserProviders = new ArrayList<>(currentUserProviders);
    // fallback to unauthenticated user
    this.currentUserProviders.add(UnauthenticatedCurrentUserProvider.INSTANCE);
  }

  @Nonnull
  @Override
  public CurrentUser get() {
    CurrentUser currentUser = null;
    for (CurrentUserProvider currentUserProvider : currentUserProviders) {
      currentUser = currentUserProvider.get();
      if (isValidUser(currentUser)) {
        return currentUser;
      }
    }
    if (currentUser == null) {
      throw new IllegalStateException("No valid current user found.");
    }
    return currentUser;
  }

  private boolean isValidUser(CurrentUser currentUser) {
    return !currentUser.getUserId().equals(UnauthenticatedCurrentUserProvider.UNAUTHENTICATED);
  }
}
