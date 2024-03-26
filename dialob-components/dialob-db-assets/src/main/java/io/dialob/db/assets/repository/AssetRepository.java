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
package io.dialob.db.assets.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;
import java.util.Optional;

public interface AssetRepository {

  @NonNull AssetBuilder createBuilder();
  @NonNull AssetQuery createQuery();

  interface AssetBuilder {
    @NonNull AssetBuilder document(@NonNull String document);
    @NonNull ObjectNode build();
  }

  interface AssetQuery {
    @NonNull AssetQuery rev(@NonNull String rev);
    @NonNull AssetQuery id(@NonNull String id);
    @NonNull AssetQuery metadata();

    void delete();
    @NonNull List<ObjectNode> list();
    Optional<ObjectNode> get();
  }
}
