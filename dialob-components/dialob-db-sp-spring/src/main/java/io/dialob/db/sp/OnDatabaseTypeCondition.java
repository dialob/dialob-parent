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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is a custom condition implementation for determining if specific
 * database-related configuration should be applied based on the active database type.
 * The condition checks if the database type specified in an {@link ConditionalOnDatabaseType}
 * annotation matches any of the database types currently configured in the application
 * environment properties.
 * <p>
 * The mechanism involves checking environment keys such as `dialob.db.database-type`,
 * `dialob.form-database.database-type`, and `dialob.questionnaire-database.database-type`,
 * and verifying if any of their values align with the database type specified in the annotation.
 * <p>
 * If a match is found, the condition is treated as fulfilled, and the configuration
 * associated with the annotated component will be activated. Otherwise, it will be ignored.
 * <p>
 * Logging is provided for cases when database values from configuration properties cannot
 * be mapped to a known {@link DialobSettings.DatabaseType}. It also suggests acceptable values
 * to assist debugging invalid configurations.
 */
@Slf4j
public class OnDatabaseTypeCondition extends SpringBootCondition {

  @Override
  public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
    var allAnnotationAttributes = metadata.getAnnotationAttributes(ConditionalOnDatabaseType.class.getName(), false);
    if (allAnnotationAttributes == null) {
      return ConditionOutcome.noMatch("ConditionalOnDatabaseType annotation missing.");
    }
    var databaseType = (DialobSettings.DatabaseType) allAnnotationAttributes.get("value");
    if (databaseType == null) {
      return ConditionOutcome.noMatch("database type not defined");
    }
    Set<DialobSettings.DatabaseType> requiredTypes = new HashSet<>();
    addType(context, requiredTypes, "dialob.db.database-type");
    addType(context, requiredTypes, "dialob.form-database.database-type");
    addType(context, requiredTypes, "dialob.questionnaire-database.database-type");

    if (requiredTypes.contains(databaseType)) {
      return ConditionOutcome.match();
    }
    return ConditionOutcome.noMatch("database type " + databaseType + " not required");
  }

  public void addType(ConditionContext context, Set<DialobSettings.DatabaseType> requiredTypes, String key) {
    String databaseTypeProperty = null;
    try {
      databaseTypeProperty = context.getEnvironment().getProperty(key);
      if (databaseTypeProperty != null) {
        requiredTypes.add(DialobSettings.DatabaseType.valueOf(databaseTypeProperty.trim().toUpperCase()));
      }
    } catch (IllegalArgumentException e) {
      LOGGER.error("Unknown database type {}={}. Acceptable values are: {}", key, databaseTypeProperty, StringUtils.join(DialobSettings.DatabaseType.values(), ","));
    }
  }
}
