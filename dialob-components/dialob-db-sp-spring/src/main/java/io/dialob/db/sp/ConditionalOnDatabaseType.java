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
package io.dialob.db.sp;

import io.dialob.settings.DialobSettings;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * This annotation is a conditional configuration mechanism used in Spring applications to
 * conditionally include beans or classes based on the active database type specified
 * in the application's environment configuration.
 *
 * The annotation uses {@link OnDatabaseTypeCondition} to implement the condition-checking logic.
 * The database type is determined by checking specific environment properties configured for the application.
 * When the database type defined by this annotation matches one of the database types specified in the
 * application’s environment, the condition is fulfilled, and the annotated configuration is activated.
 *
 * The provided {@link DialobSettings.DatabaseType} value indicates the database type to compare
 * against the active database type(s). The supported types are defined in {@link DialobSettings.DatabaseType}.
 *
 * Use this annotation on application classes or methods where desired logic or beans should
 * only be included for specific database types, such as MONGODB or JDBC.
 *
 * This annotation can be applied at the type or method level.
 *
 * Annotation parameters:
 * - `value`: Specifies the required {@link DialobSettings.DatabaseType} for the conditional activation.
 *
 * Example database-related configuration keys that influence the condition include:
 * - `dialob.db.database-type`
 * - `dialob.form-database.database-type`
 * - `dialob.questionnaire-database.database-type`
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Documented
@Conditional(OnDatabaseTypeCondition.class)
public @interface ConditionalOnDatabaseType {

  DialobSettings.DatabaseType value();

}
