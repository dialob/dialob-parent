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
package io.dialob.form.service.rest;

import io.dialob.api.form.FormTag;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.security.tenant.CurrentTenant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class FormTagsRestServiceController implements FormTagsRestService {

  private final Optional<FormVersionControlDatabase> formVersionControlDatabase;

  private final CurrentTenant currentTenant;

  public FormTagsRestServiceController(Optional<FormVersionControlDatabase> formVersionControlDatabase,
                                    CurrentTenant currentTenant) {
    this.formVersionControlDatabase = formVersionControlDatabase;
    this.currentTenant = currentTenant;
  }

  @Override
  public ResponseEntity<List<FormTag>> getTags(String formName, String formId, String name) {
    return formVersionControlDatabase.map(
        versionControlDatabase -> ResponseEntity.ok(versionControlDatabase.queryTags(currentTenant.getId(), formName, formId, name, FormTag.Type.NORMAL)))
      .orElse(ResponseEntity.notFound().build());
  }

}
