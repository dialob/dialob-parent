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
package io.dialob.db.jdbi.model;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.api.form.FormTag;
import org.apache.commons.lang3.StringUtils;
import org.jdbi.v3.core.enums.EnumByName;
import org.jdbi.v3.core.mapper.Nested;

import java.time.Instant;
import java.util.Objects;

public record FormRev(
  Id id,
  @Nested("ref") Id ref,
  Instant created,
  Instant updated,
  @Nullable @Nested("form_document") FormDocument.Id formDocument,
  @Nullable String description,
  @Nullable @EnumByName FormTag.Type type,
  @Nullable String creator
) {

  public record Id(
    String tenantId,
    String formName,
    String name
  ) { }

  public FormRev {
    if (ref != null && !Objects.equals(StringUtils.trim(id.tenantId()), StringUtils.trim(ref.tenantId()))) {
      throw new IllegalArgumentException("Form and referred name must belong to same tenant");
    }
  }


/*
foreign-key constraints:
  "form_rev_form_document_id_fkey" FOREIGN KEY (form_document_id) REFERENCES form_document(id) ON DELETE RESTRICT
  "form_rev_form_name_fkey" FOREIGN KEY (tenant_id, form_name) REFERENCES form(tenant_id, name)
  "form_rev_ref_name_fkey" FOREIGN KEY (tenant_id, form_name, ref_name) REFERENCES form_rev(tenant_id, form_name, name) ON UPDATE RESTRICT ON DELETE RESTRICT

referenced by:
    TABLE "form_rev" CONSTRAINT "form_rev_ref_name_fkey" FOREIGN KEY (tenant_id, form_name, ref_name) REFERENCES form_rev(tenant_id, form_name, name) ON UPDATE RESTRICT ON DELETE RESTRICT

 form_name        | character varying(128)      |           | not null |
 name             | character varying(255)      |           | not null | ''::character varying
 created          | timestamp without time zone |           | not null | CURRENT_TIMESTAMP
 updated          | timestamp without time zone |           | not null | CURRENT_TIMESTAMP
 form_document_id | uuid                        |           | not null |
 tenant_id        | character(36)               |           | not null | ''::bpchar
 description      | character varying(255)      |           |          | NULL::character varying
 type             | character(7)                |           |          | 'NORMAL'::bpchar
 ref_name         | character varying(128)      |           |          | NULL::character varying
 creator          | text                        |           |          |
*/
}
