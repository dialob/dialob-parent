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
package io.dialob.db.spi.spring;

import edu.umd.cs.findbugs.annotations.NonNull;
import lombok.Getter;
import org.springframework.data.annotation.Version;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractDocumentDatabase<T> {

  @Getter
  private final Class<? extends T> documentClass;

  private final ReflectionEntityInformation idEntityInformation;

  private final ReflectionEntityInformation revEntityInformation;

  protected AbstractDocumentDatabase(@NonNull Class<? extends T> documentClass) {
    this.documentClass = documentClass;
    this.idEntityInformation = new ReflectionEntityInformation(documentClass);
    this.revEntityInformation = new ReflectionEntityInformation(documentClass, Version.class);
    Assert.isTrue(idEntityInformation.getIdType() == String.class, "id type is not String on type " + documentClass.getCanonicalName());
    Assert.isTrue(revEntityInformation.getIdType() == String.class, "version type is not String on type " + documentClass.getCanonicalName());
  }

  @Nullable
  protected String id(@NonNull T document) {
    return (String) idEntityInformation.getId(document);
  }

  @Nullable
  protected String rev(@NonNull T document) {
    return (String) revEntityInformation.getId(document);
  }

  @NonNull
  protected abstract T updateDocumentId(@NonNull T document, String id);

  @NonNull
  protected abstract T updateDocumentRev(@NonNull T document, String rev);

}
