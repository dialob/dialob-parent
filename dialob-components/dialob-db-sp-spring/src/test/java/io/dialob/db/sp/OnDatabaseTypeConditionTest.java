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

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OnDatabaseTypeConditionTest {

  @Test
  void shouldNotMatchWhenAnnotationIsNotFound() {
    var condition = new OnDatabaseTypeCondition();
    var context = mock(ConditionContext.class);
    var metadata = mock(AnnotatedTypeMetadata.class);
    when(metadata.getAnnotationAttributes(ConditionalOnDatabaseType.class.getName(), false)).thenReturn(null);
    var outcome = condition.getMatchOutcome(context, metadata);
    assertFalse(outcome.isMatch());
    assertEquals("ConditionalOnDatabaseType annotation missing.", outcome.getMessage());
  }

  @Test
  void shouldNotMatchValueIsMissing() {
    var condition = new OnDatabaseTypeCondition();
    var context = mock(ConditionContext.class);
    var metadata = mock(AnnotatedTypeMetadata.class);
    var attrs = new HashMap<String,Object>();
    when(metadata.getAnnotationAttributes(ConditionalOnDatabaseType.class.getName(), false)).thenReturn(attrs);
    var outcome = condition.getMatchOutcome(context, metadata);
    assertFalse(outcome.isMatch());
    assertEquals("database type not defined", outcome.getMessage());
  }
}
