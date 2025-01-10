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
package io.dialob.db.jdbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.form.service.api.FormVersionControlDatabase;
import io.dialob.questionnaire.service.api.QuestionnaireDatabase;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.ResysSecurityConstants;
import io.dialob.security.tenant.Tenant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.function.Predicate;

public interface JdbcBackendTest {

  ObjectMapper objectMapper = new ObjectMapper();

  Predicate<String> IS_ANY_TENANT_PREDICATE = tenantId -> ResysSecurityConstants.DEFAULT_TENANT.id().equals(tenantId);

  DataSource getDataSource();

  JdbcFormDatabase getJdbcFormDatabase();

  JdbcTemplate getJdbcTemplate();

  TransactionTemplate getTransactionTemplate();

  FormVersionControlDatabase getJdbcFormVersionControlDatabase();

  QuestionnaireDatabase getQuestionnaireDatabase();

  Tenant setActiveTenant(String tenantId);

  Tenant resetTenant();

  CurrentTenant getCurrentTenant();

}
