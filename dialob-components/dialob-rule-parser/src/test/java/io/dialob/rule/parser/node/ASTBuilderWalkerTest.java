package io.dialob.rule.parser.node;

import io.dialob.rule.parser.DialobRuleParser;
import io.dialob.rule.parser.api.CompilerErrorCode;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.api.VariableFinder;
import io.dialob.rule.parser.api.VariableNotDefinedException;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ASTBuilderWalkerTest {

  @Test
  void shouldLogUnknownVariableAndIdWhenVariableIsNotFound() throws Exception {
    VariableFinder variableFinder = mock();
    ErrorLogger errorLogger = mock();

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("x");
    when(ctx.var.getStartIndex()).thenReturn(0);
    when(ctx.var.getStopIndex()).thenReturn(1);

    when(variableFinder.mapAlias("x")).thenReturn("x");
    when(variableFinder.typeOf("x")).thenThrow(new VariableNotDefinedException("x"));

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);
    astBuilderWalker.enterIdExprRule(ctx);

    verify(variableFinder).mapAlias("x");
    verify(variableFinder).typeOf("x");
    verify(ctx.var, atLeastOnce()).getStartIndex();
    verify(ctx.var, atLeastOnce()).getStopIndex();
    verify(ctx.var).getText();
    verify(errorLogger).logError(CompilerErrorCode.UNKNOWN_VARIABLE, new Object[]{"x"}, Span.of(0, 1));

    verifyNoMoreInteractions(variableFinder, ctx, ctx.var, errorLogger);
  }

  @Test
  void enterIdExprRule_shouldCreateIdExprNodeWithValueType() throws Exception {
    VariableFinder variableFinder = mock();
    ErrorLogger errorLogger = mock();

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("myVar");
    when(ctx.var.getStartIndex()).thenReturn(5);
    when(ctx.var.getStopIndex()).thenReturn(10);

    when(variableFinder.mapAlias("myVar")).thenReturn("myVar");
    when(variableFinder.typeOf("myVar")).thenReturn(ValueType.STRING);
    when(variableFinder.findVariableScope("myVar")).thenReturn(Optional.empty());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);

    assertNull(astBuilderWalker.getBuilder().getTopNode());
    astBuilderWalker.enterIdExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder().getTopNode());
    assertTrue(astBuilderWalker.getBuilder().getTopNode() instanceof IdExprNode);
    IdExprNode node = (IdExprNode) astBuilderWalker.getBuilder().getTopNode();
    assertEquals("myVar", node.getId());
    assertEquals(ValueType.STRING, node.getValueType());

    verify(variableFinder).mapAlias("myVar");
    verify(variableFinder).typeOf("myVar");
    verify(variableFinder).findVariableScope("myVar");
    verifyNoInteractions(errorLogger);
  }

  @Test
  void enterIdExprRule_shouldCreateIdExprNodeWithScopeWhenScopeExists() throws Exception {
    VariableFinder variableFinder = mock();
    ErrorLogger errorLogger = mock();

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("scopedVar");
    when(ctx.var.getStartIndex()).thenReturn(0);
    when(ctx.var.getStopIndex()).thenReturn(9);

    when(variableFinder.mapAlias("scopedVar")).thenReturn("scopedVar");
    when(variableFinder.typeOf("scopedVar")).thenReturn(ValueType.INTEGER);
    when(variableFinder.findVariableScope("scopedVar")).thenReturn(Optional.of("myScope"));

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);
    astBuilderWalker.enterIdExprRule(ctx);

    verify(variableFinder).mapAlias("scopedVar");
    verify(variableFinder).typeOf("scopedVar");
    verify(variableFinder).findVariableScope("scopedVar");
    verifyNoInteractions(errorLogger);
  }

  @Test
  void enterIdExprRule_shouldMapAliasToVariableId() throws Exception {
    VariableFinder variableFinder = mock();
    ErrorLogger errorLogger = mock();

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("aliasName");
    when(ctx.var.getStartIndex()).thenReturn(0);
    when(ctx.var.getStopIndex()).thenReturn(8);

    when(variableFinder.mapAlias("aliasName")).thenReturn("actualVariableName");
    when(variableFinder.typeOf("actualVariableName")).thenReturn(ValueType.BOOLEAN);
    when(variableFinder.findVariableScope("actualVariableName")).thenReturn(Optional.empty());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);
    astBuilderWalker.enterIdExprRule(ctx);

    verify(variableFinder).mapAlias("aliasName");
    verify(variableFinder).typeOf("actualVariableName");
    verify(variableFinder).findVariableScope("actualVariableName");
    verifyNoInteractions(errorLogger);
  }

  @Test
  void exitIdExprRule_shouldPopBuilder() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.IdExprRuleContext ctx = mock();

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());

    // First enter to create a node
    astBuilderWalker.getBuilder().constExprNode("test", null, ValueType.STRING, Span.of(0, 4));
    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitIdExprRule(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithStringValue() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.STRING;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("'hello'");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(6);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());

    assertNull(astBuilderWalker.getBuilder().getTopNode());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder().getTopNode());
    assertTrue(astBuilderWalker.getBuilder().getTopNode() instanceof ConstExprNode);
    ConstExprNode node = (ConstExprNode) astBuilderWalker.getBuilder().getTopNode();
    assertEquals("hello", node.getValue()); // Quotes should be removed
    assertEquals(ValueType.STRING, node.getValueType());
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithDoubleQuotedString() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.STRING;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("\"world\"");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(6);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithIntegerValue() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.INTEGER;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("42");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(1);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithDecimalValue() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.DECIMAL;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("3.14");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(3);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithBooleanValue() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.BOOLEAN;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("true");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(3);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithUnit() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.unit = mock(Token.class);
    ctx.type = ValueType.DURATION;

    when(ctx.value.getText()).thenReturn("5");
    when(ctx.unit.getText()).thenReturn("days");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.unit);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.unit.getStopIndex()).thenReturn(5);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
  }

  @Test
  void enterConstExprRule_shouldRemoveQuotesFromStringValue() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.STRING;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("'test value'");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(11);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());

    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder().getTopNode());
    assertTrue(astBuilderWalker.getBuilder().getTopNode() instanceof ConstExprNode);
    ConstExprNode node = (ConstExprNode) astBuilderWalker.getBuilder().getTopNode();
    assertEquals("test value", node.getValue()); // Quotes should be removed
  }

  @Test
  void enterConstExprRule_shouldNotRemoveQuotesFromNonStringValue() {
    VariableFinder variableFinder = mock();

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.INTEGER;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("100");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(2);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
  }

  @Test
  void exitConstExprRule_shouldPopBuilder() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.ConstExprRuleContext ctx = mock();

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(variableFinder, new HashMap<>());

    // First enter to create a node
    astBuilderWalker.getBuilder().constExprNode("123", null, ValueType.INTEGER, Span.of(0, 2));
    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitConstExprRule(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);
  }

}
