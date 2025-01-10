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
package io.dialob.security.uaa.spi;

import feign.FeignException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UaaServiceBase {

  private final UaaClient uaaClient;

  public UaaServiceBase(UaaClient uaaClient) {
    this.uaaClient = uaaClient;
  }

  protected UaaClient getUaaClient() {
    return uaaClient;
  }

  protected <T> Optional<T> get(Function<UaaClient,T> op) {
    try {
      return Optional.of(op.apply(getUaaClient()));
    } catch (FeignException.NotFound notFound) {
      return Optional.empty();
    }
  }

  protected <T> List<T> list(Function<UaaClient,List<T>> op) {
    try {
      return op.apply(getUaaClient());
    } catch (FeignException.NotFound notFound) {
      return Collections.emptyList();
    }
  }
}
