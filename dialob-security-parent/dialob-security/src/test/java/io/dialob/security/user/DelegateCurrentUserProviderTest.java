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

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DelegateCurrentUserProviderTest {

  private static final CurrentUser AUTHENTICATED_USER = new CurrentUser("user123", "John Doe", "John", "Doe", "john@example.com");
  private static final CurrentUser ANOTHER_USER = new CurrentUser("user456", "Jane Smith", "Jane", "Smith", "jane@example.com");

  @Test
  void shouldReturnUserFromFirstProvider() {
    CurrentUserProvider provider1 = () -> AUTHENTICATED_USER;
    CurrentUserProvider provider2 = () -> ANOTHER_USER;

    DelegateCurrentUserProvider delegate = new DelegateCurrentUserProvider(provider1, provider2);

    CurrentUser result = delegate.get();

    assertEquals("user123", result.userId());
    assertEquals("John Doe", result.displayName());
  }

  @Test
  void shouldDelegateToSecondProviderWhenFirstReturnsUnauthenticated() {
    CurrentUserProvider provider1 = () -> UnauthenticatedCurrentUserProvider.UNAUTHENTICATED_USER;
    CurrentUserProvider provider2 = () -> AUTHENTICATED_USER;

    DelegateCurrentUserProvider delegate = new DelegateCurrentUserProvider(provider1, provider2);

    CurrentUser result = delegate.get();

    assertEquals("user123", result.userId());
    assertEquals("John Doe", result.displayName());
  }

  @Test
  void shouldReturnUnauthenticatedUserWhenAllProvidersReturnUnauthenticated() {
    CurrentUserProvider provider1 = () -> UnauthenticatedCurrentUserProvider.UNAUTHENTICATED_USER;
    CurrentUserProvider provider2 = () -> UnauthenticatedCurrentUserProvider.UNAUTHENTICATED_USER;

    DelegateCurrentUserProvider delegate = new DelegateCurrentUserProvider(provider1, provider2);

    CurrentUser result = delegate.get();

    assertEquals("unauthenticated", result.userId());
  }

  @Test
  void shouldWorkWithCollectionConstructor() {
    CurrentUserProvider provider1 = () -> UnauthenticatedCurrentUserProvider.UNAUTHENTICATED_USER;
    CurrentUserProvider provider2 = () -> AUTHENTICATED_USER;

    DelegateCurrentUserProvider delegate = new DelegateCurrentUserProvider(Arrays.asList(provider1, provider2));

    CurrentUser result = delegate.get();

    assertEquals("user123", result.userId());
  }

  @Test
  void shouldThrowNullPointerExceptionWhenProvidersReturnNull() {
    CurrentUserProvider provider1 = () -> null;
    CurrentUserProvider provider2 = () -> null;

    DelegateCurrentUserProvider delegate = new DelegateCurrentUserProvider(provider1, provider2);

    assertThrows(NullPointerException.class, delegate::get);
  }

  @Test
  void shouldReturnFirstValidUserInChain() {
    CurrentUserProvider provider1 = () -> UnauthenticatedCurrentUserProvider.UNAUTHENTICATED_USER;
    CurrentUserProvider provider2 = () -> AUTHENTICATED_USER;
    CurrentUserProvider provider3 = () -> ANOTHER_USER;

    DelegateCurrentUserProvider delegate = new DelegateCurrentUserProvider(provider1, provider2, provider3);

    CurrentUser result = delegate.get();

    assertEquals("user123", result.userId());
    assertEquals("John Doe", result.displayName());
  }

  @Test
  void shouldWorkWithNoProvidersAndFallbackToUnauthenticated() {
    DelegateCurrentUserProvider delegate = new DelegateCurrentUserProvider();

    CurrentUser result = delegate.get();

    assertEquals("unauthenticated", result.userId());
  }
}
