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
