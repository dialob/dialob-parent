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
package io.dialob.session.engine.program.expr.arith;

import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.ProgramBuilder;
import io.dialob.session.engine.program.expr.OutputFormatter;
import io.dialob.session.engine.program.model.Label;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ImmutableValueSetId;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ValueSetState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class LocalizedLabelOperatorTest {

  @Mock
  ProgramBuilder programBuilder;
  @Mock
  EvalContext context;
  @Mock
  OutputFormatter outputFormatter;

  @BeforeEach
  public void beforeEach() {
    MockitoAnnotations.initMocks(this);
    when(programBuilder.findValueSetIdForItem(any(ItemId.class))).thenReturn(Optional.empty());
    when(outputFormatter.format(any())).then(invocation -> String.valueOf(invocation.<Object>getArgument(0)));
    when(context.getOutputFormatter()).thenReturn(outputFormatter);
  }

  @Test
  void shouldReturnNullWhenLabelIsNotDefined() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Collections.emptyMap()));
    when(context.getLanguage()).thenReturn("fi");
    assertNull(operator.eval(context));
    verify(context).getLanguage();
    verifyNoMoreInteractions(programBuilder, context);
  }

  @Test
  void shouldReturnLocalizeLabel() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko","en","Title")));
    when(context.getLanguage()).thenReturn("fi").thenReturn("en");
    assertEquals("Otsikko", operator.eval(context));
    assertEquals("Title", operator.eval(context));
    verify(context, times(2)).getLanguage();
    verifyNoMoreInteractions(programBuilder, context);
  }

  @Test
  void shouldExpandVariablesNonExistentExapndsToNull() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1}")));
    when(context.getLanguage()).thenReturn("fi");
    assertEquals("Otsikko null", operator.eval(context));
    verify(context, times(1)).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verifyNoMoreInteractions(programBuilder, context);
  }

  private ItemId ref(String var1) {
    return IdUtils.toId(var1);
  }


  @Test
  void shouldExpandStringVariables() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn("hello");
    assertEquals("Otsikko hello", operator.eval(context));
    verify(context, times(1)).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verify(context).getOutputFormatter();
    verifyNoMoreInteractions(programBuilder, context);
  }

  @Test
  void shouldExpandNumberVariablesWithFormmater() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1:#,##0}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn(123000);
    assertEquals("Otsikko 123 000", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verifyNoMoreInteractions(programBuilder, context);
  }
  @Test
  void shouldExpandDecimalVariablesWithFormmater() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1:#,##0.00}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn(BigDecimal.valueOf(123000));
    assertEquals("Otsikko 123 000,00", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verifyNoMoreInteractions(programBuilder, context);
  }
  @Test
  void shouldExpandNumberlVariablesWithoutFormmater() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn(123000);
    assertEquals("Otsikko 123000", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(context).getOutputFormatter();
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verifyNoMoreInteractions(programBuilder, context);
  }
  @Test
  void shouldExpandDecimallVariablesWithoutFormmater() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn(BigDecimal.valueOf(123000.01));
    assertEquals("Otsikko 123000.01", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(context).getOutputFormatter();
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verifyNoMoreInteractions(programBuilder, context);
  }
  @Test
  void shouldInterpolateSelectionToValue() {
    when(programBuilder.findValueSetIdForItem(IdUtils.toId("var1"))).thenReturn(Optional.of("vs1"));

    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn("x1");
    ValueSetState valueSet = Mockito.mock(ValueSetState.class);
    when(valueSet.getEntries()).thenReturn(List.of(ValueSetState.Entry.of("x1","Choice 1")));
    when(context.getValueSetState(ImmutableValueSetId.of("vs1"))).thenReturn(Optional.of(valueSet));
    assertEquals("Otsikko Choice 1", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(context).getValueSetState(ImmutableValueSetId.of("vs1"));
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verify(valueSet).getEntries();
    verifyNoMoreInteractions(programBuilder, context, valueSet);
  }
  @Test
  void shouldInterpolateSelectionToLowerCaseValue() {
    when(programBuilder.findValueSetIdForItem(IdUtils.toId("var1"))).thenReturn(Optional.of("vs1"));

    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1:lowercase}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn("x1");
    ValueSetState valueSet = Mockito.mock(ValueSetState.class);
    when(valueSet.getEntries()).thenReturn(List.of(ValueSetState.Entry.of("x1","Choice 1")));
    when(context.getValueSetState(ImmutableValueSetId.of("vs1"))).thenReturn(Optional.of(valueSet));
    assertEquals("Otsikko choice 1", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(context).getValueSetState(ImmutableValueSetId.of("vs1"));
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verify(valueSet).getEntries();
    verifyNoMoreInteractions(programBuilder, context, valueSet);
  }

  @Test
  void shouldInterpolateSelectionToUpperCaseValue() {
    when(programBuilder.findValueSetIdForItem(IdUtils.toId("var1"))).thenReturn(Optional.of("vs1"));

    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1:uppercase}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn("x1");
    ValueSetState valueSet = Mockito.mock(ValueSetState.class);
    when(valueSet.getEntries()).thenReturn(List.of(ValueSetState.Entry.of("x1","Choice 1")));
    when(context.getValueSetState(ImmutableValueSetId.of("vs1"))).thenReturn(Optional.of(valueSet));
    assertEquals("Otsikko CHOICE 1", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(context).getValueSetState(ImmutableValueSetId.of("vs1"));
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verify(valueSet).getEntries();
    verifyNoMoreInteractions(programBuilder, context, valueSet);
  }
  @Test
  void shouldInterpolateSelectionToKeyWhenFormatIsKey() {
    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1:key}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn("x1");
    assertEquals("Otsikko x1", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(context).getOutputFormatter();
    verifyNoMoreInteractions(programBuilder, context);
  }

  @Test
  void shouldInterpolateMultichoiceSelectionToValue() {
    when(programBuilder.findValueSetIdForItem(IdUtils.toId("var1"))).thenReturn(Optional.of("vs1"));

    LocalizedLabelOperator operator = LocalizedLabelOperator.createLocalizedLabelOperator(programBuilder, Label.createLabel(Map.of("fi","Otsikko {var1}")));
    when(context.getLanguage()).thenReturn("fi");
    when(context.getItemValue(ref("var1"))).thenReturn(Arrays.asList("x1", "x2"));

    ValueSetState valueSet = Mockito.mock(ValueSetState.class);
    when(valueSet.getEntries()).thenReturn(List.of(
        ValueSetState.Entry.of("x1","Choice 1"),
        ValueSetState.Entry.of("x2","Choice 2")
      )
    );
    when(context.getValueSetState(ImmutableValueSetId.of("vs1"))).thenReturn(Optional.of(valueSet));

    assertEquals("Otsikko Choice 1, Choice 2", operator.eval(context));
    verify(context, atLeastOnce()).getLanguage();
    verify(context).getItemValue(ref("var1"));
    verify(context).getValueSetState(ImmutableValueSetId.of("vs1"));
    verify(programBuilder).findValueSetIdForItem(any(ItemId.class));
    verify(valueSet).getEntries();
    verifyNoMoreInteractions(programBuilder, context, valueSet);
  }

}
