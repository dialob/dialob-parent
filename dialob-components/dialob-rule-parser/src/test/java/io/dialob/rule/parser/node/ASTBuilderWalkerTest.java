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
    var builder = spy(new ASTBuilder());

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("x");
    when(ctx.var.getStartIndex()).thenReturn(0);
    when(ctx.var.getStopIndex()).thenReturn(1);

    when(variableFinder.mapAlias("x")).thenReturn("x");
    when(variableFinder.typeOf("x")).thenThrow(new VariableNotDefinedException("x"));

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);
    astBuilderWalker.enterIdExprRule(ctx);

    verify(variableFinder).mapAlias("x");
    verify(variableFinder).typeOf("x");
    verify(ctx.var, atLeastOnce()).getStartIndex();
    verify(ctx.var, atLeastOnce()).getStopIndex();
    verify(ctx.var).getText();
    verify(errorLogger).logError(CompilerErrorCode.UNKNOWN_VARIABLE, new Object[]{"x"}, Span.of(0, 1));
    verify(builder).idExprNode(isNull(), eq("x"), isNull(), any(Span.class));
    verify(builder).idExprNode(isNull(), isNull(), eq("x"), isNull(), any(Span.class));

    verifyNoMoreInteractions(variableFinder, ctx, ctx.var, errorLogger, builder);
  }

  @Test
  void enterIdExprRule_shouldCreateIdExprNodeWithValueType() throws Exception {
    VariableFinder variableFinder = mock();
    ErrorLogger errorLogger = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("myVar");
    when(ctx.var.getStartIndex()).thenReturn(5);
    when(ctx.var.getStopIndex()).thenReturn(10);

    when(variableFinder.mapAlias("myVar")).thenReturn("myVar");
    when(variableFinder.typeOf("myVar")).thenReturn(ValueType.STRING);
    when(variableFinder.findVariableScope("myVar")).thenReturn(Optional.empty());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
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
    verify(builder).idExprNode(isNull(), eq("myVar"), eq(ValueType.STRING), any(Span.class));
    verify(builder).idExprNode(isNull(), isNull(), eq("myVar"), eq(ValueType.STRING), any(Span.class));
    verify(builder, times(4)).getTopNode();
    verifyNoInteractions(errorLogger);
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterIdExprRule_shouldCreateIdExprNodeWithScopeWhenScopeExists() throws Exception {
    VariableFinder variableFinder = mock();
    ErrorLogger errorLogger = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("scopedVar");
    when(ctx.var.getStartIndex()).thenReturn(0);
    when(ctx.var.getStopIndex()).thenReturn(9);

    when(variableFinder.mapAlias("scopedVar")).thenReturn("scopedVar");
    when(variableFinder.typeOf("scopedVar")).thenReturn(ValueType.INTEGER);
    when(variableFinder.findVariableScope("scopedVar")).thenReturn(Optional.of("myScope"));

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);
    astBuilderWalker.enterIdExprRule(ctx);

    verify(variableFinder).mapAlias("scopedVar");
    verify(variableFinder).typeOf("scopedVar");
    verify(variableFinder).findVariableScope("scopedVar");
    verify(builder).idExprNode(isNull(), eq("myScope"), eq("scopedVar"), eq(ValueType.INTEGER), any(Span.class));
    verifyNoInteractions(errorLogger);
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterIdExprRule_shouldMapAliasToVariableId() throws Exception {
    VariableFinder variableFinder = mock();
    ErrorLogger errorLogger = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.IdExprRuleContext ctx = mock();
    ctx.var = mock();
    when(ctx.var.getText()).thenReturn("aliasName");
    when(ctx.var.getStartIndex()).thenReturn(0);
    when(ctx.var.getStopIndex()).thenReturn(8);

    when(variableFinder.mapAlias("aliasName")).thenReturn("actualVariableName");
    when(variableFinder.typeOf("actualVariableName")).thenReturn(ValueType.BOOLEAN);
    when(variableFinder.findVariableScope("actualVariableName")).thenReturn(Optional.empty());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);
    astBuilderWalker.enterIdExprRule(ctx);

    verify(variableFinder).mapAlias("aliasName");
    verify(variableFinder).typeOf("actualVariableName");
    verify(variableFinder).findVariableScope("actualVariableName");
    verify(builder).idExprNode(isNull(), eq("actualVariableName"), eq(ValueType.BOOLEAN), any(Span.class));
    verify(builder).idExprNode(isNull(), isNull(), eq("actualVariableName"), eq(ValueType.BOOLEAN), any(Span.class));
    verifyNoInteractions(errorLogger);
    verifyNoMoreInteractions(builder);
  }

  @Test
  void exitIdExprRule_shouldPopBuilder() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.IdExprRuleContext ctx = mock();
    var builder = spy(new ASTBuilder());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());

    // First enter to create a node
    astBuilderWalker.getBuilder().constExprNode("test", null, ValueType.STRING, Span.of(0, 4));
    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitIdExprRule(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);
    verify(builder).constExprNode(eq("test"), isNull(), eq(ValueType.STRING), any(Span.class));
    verify(builder, times(2)).getTopNode();
    verify(builder).closeExpr();
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithStringValue() {
    VariableFinder variableFinder = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.STRING;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("'hello'");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(6);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());

    assertNull(astBuilderWalker.getBuilder().getTopNode());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder().getTopNode());
    assertTrue(astBuilderWalker.getBuilder().getTopNode() instanceof ConstExprNode);
    ConstExprNode node = (ConstExprNode) astBuilderWalker.getBuilder().getTopNode();
    assertEquals("hello", node.getValue()); // Quotes should be removed
    assertEquals(ValueType.STRING, node.getValueType());

    verify(builder).constExprNode(eq("hello"), isNull(), eq(ValueType.STRING), any(Span.class));
    verify(builder, times(4)).getTopNode();
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithDoubleQuotedString() {
    VariableFinder variableFinder = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.STRING;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("\"world\"");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(6);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
    verify(builder).constExprNode(eq("world"), isNull(), eq(ValueType.STRING), any(Span.class));
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithIntegerValue() {
    VariableFinder variableFinder = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.INTEGER;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("42");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(1);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
    verify(builder).constExprNode(eq("42"), isNull(), eq(ValueType.INTEGER), any(Span.class));
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithDecimalValue() {
    VariableFinder variableFinder = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.DECIMAL;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("3.14");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(3);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
    verify(builder).constExprNode(eq("3.14"), isNull(), eq(ValueType.DECIMAL), any(Span.class));
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithBooleanValue() {
    VariableFinder variableFinder = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.BOOLEAN;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("true");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(3);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
    verify(builder).constExprNode(eq("true"), isNull(), eq(ValueType.BOOLEAN), any(Span.class));
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldCreateConstExprNodeWithUnit() {
    VariableFinder variableFinder = mock();
    ASTBuilder builder = spy(new ASTBuilder());

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

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
    verify(builder).constExprNode(eq("5"), eq("days"), eq(ValueType.DURATION), any(Span.class));
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldRemoveQuotesFromStringValue() {
    VariableFinder variableFinder = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.STRING;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("'test value'");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(11);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());

    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder().getTopNode());
    assertTrue(astBuilderWalker.getBuilder().getTopNode() instanceof ConstExprNode);
    ConstExprNode node = (ConstExprNode) astBuilderWalker.getBuilder().getTopNode();
    assertEquals("test value", node.getValue()); // Quotes should be removed

    verify(builder).constExprNode(eq("test value"), isNull(), eq(ValueType.STRING), any(Span.class));
    verify(builder, times(3)).getTopNode();
    verifyNoMoreInteractions(builder);
  }

  @Test
  void enterConstExprRule_shouldNotRemoveQuotesFromNonStringValue() {
    VariableFinder variableFinder = mock();
    var builder = spy(new ASTBuilder());

    DialobRuleParser.ConstExprRuleContext ctx = mock();
    ctx.value = mock(Token.class);
    ctx.type = ValueType.INTEGER;
    ctx.unit = null;

    when(ctx.value.getText()).thenReturn("100");
    when(ctx.getStart()).thenReturn(ctx.value);
    when(ctx.getStop()).thenReturn(ctx.value);
    when(ctx.value.getStartIndex()).thenReturn(0);
    when(ctx.value.getStopIndex()).thenReturn(2);

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.enterConstExprRule(ctx);

    assertNotNull(astBuilderWalker.getBuilder());
    verify(builder).constExprNode(eq("100"), isNull(), eq(ValueType.INTEGER), any(Span.class));
    verifyNoMoreInteractions(builder);
  }

  @Test
  void exitConstExprRule_shouldPopBuilder() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.ConstExprRuleContext ctx = mock();
    var builder = spy(new ASTBuilder());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());

    // First enter to create a node
    astBuilderWalker.getBuilder().constExprNode("123", null, ValueType.INTEGER, Span.of(0, 2));
    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitConstExprRule(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);

    verify(builder).constExprNode(eq("123"), isNull(), eq(ValueType.INTEGER), any(Span.class));
    verify(builder, times(2)).getTopNode();
    verify(builder).closeExpr();
    verifyNoMoreInteractions(builder);
  }

  @Test
  void exitInfixExprRule_shouldPopBuilder() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.InfixExprContext ctx = mock();
    ctx.op = mock(Token.class);
    ctx.start = mock(Token.class);
    ctx.stop = mock(Token.class);

    when(ctx.op.getText()).thenReturn("+");
    when(ctx.getStart()).thenReturn(ctx.start);
    when(ctx.getStop()).thenReturn(ctx.stop);
    when(ctx.start.getStartIndex()).thenReturn(0);
    when(ctx.stop.getStopIndex()).thenReturn(10);

    var builder = spy(new ASTBuilder());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());

    // First enter to create a node
    astBuilderWalker.enterInfixExpr(ctx);

    astBuilderWalker.getBuilder().constExprNode("123", null, ValueType.INTEGER, Span.of(0, 2)).closeExpr();
    astBuilderWalker.getBuilder().constExprNode("123", null, ValueType.INTEGER, Span.of(0, 2)).closeExpr();;
    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitInfixExpr(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);

    var root = astBuilderWalker.getBuilder().build();
    assertEquals(ValueType.INTEGER, root.getValueType());

    verify(builder, times(2)).constExprNode(eq("123"), isNull(), eq(ValueType.INTEGER), any(Span.class));
    verify(builder).callExprNode(eq(new NodeOperator("+", NodeOperator.Category.INFIX)), isNull(), any(Span.class));
    verify(builder).infixExprNode(eq("+"), any(Span.class));

    verify(builder, atLeastOnce()).getTopNode();
    verify(builder, atLeastOnce()).closeExpr();
    verify(builder, atLeastOnce()).setValueType(any());
    verify(builder).build();
    verifyNoMoreInteractions(builder);
  }

  @Test
  void exitInfixExprRule_substractTime() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.InfixExprContext ctx = mock();
    ctx.op = mock(Token.class);
    ctx.start = mock(Token.class);
    ctx.stop = mock(Token.class);

    when(ctx.op.getText()).thenReturn("-");
    when(ctx.getStart()).thenReturn(ctx.start);
    when(ctx.getStop()).thenReturn(ctx.stop);
    when(ctx.start.getStartIndex()).thenReturn(0);
    when(ctx.stop.getStopIndex()).thenReturn(10);

    var builder = spy(new ASTBuilder());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());

    // First enter to create a node
    astBuilderWalker.enterInfixExpr(ctx);

    astBuilderWalker.getBuilder().constExprNode("01:00:00", null, ValueType.STRING, Span.of(0, 2)).closeExpr();
    astBuilderWalker.getBuilder().constExprNode("12:00:00", null, ValueType.TIME, Span.of(0, 2)).closeExpr();;
    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitInfixExpr(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);

    var root = astBuilderWalker.getBuilder().build();
    assertEquals(ValueType.DURATION, root.getValueType());

    verify(builder).constExprNode(eq("01:00:00"), isNull(), eq(ValueType.STRING), any(Span.class));
    verify(builder).constExprNode(eq("12:00:00"), isNull(), eq(ValueType.TIME), any(Span.class));
    verify(builder).callExprNode(eq(new NodeOperator("-", NodeOperator.Category.INFIX)), isNull(), any(Span.class));
    verify(builder).infixExprNode(eq("-"), any(Span.class));

    verify(builder, atLeastOnce()).getTopNode();
    verify(builder, atLeastOnce()).closeExpr();
    verify(builder, atLeastOnce()).setValueType(any());
    verify(builder).build();
    verifyNoMoreInteractions(builder);
  }

  @Test
  void exitInfixExprRule_substractTimeNoMatch() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.InfixExprContext ctx = mock();
    ErrorLogger errorLogger = mock();
    ctx.op = mock(Token.class);
    ctx.start = mock(Token.class);
    ctx.stop = mock(Token.class);

    when(ctx.op.getText()).thenReturn("-");
    when(ctx.getStart()).thenReturn(ctx.start);
    when(ctx.getStop()).thenReturn(ctx.stop);
    when(ctx.start.getStartIndex()).thenReturn(0);
    when(ctx.stop.getStopIndex()).thenReturn(10);

    var builder = spy(new ASTBuilder());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);

    // First enter to create a node
    astBuilderWalker.enterInfixExpr(ctx);

    astBuilderWalker.getBuilder().constExprNode("AAA", null, ValueType.STRING, Span.of(0, 2)).closeExpr();
    astBuilderWalker.getBuilder().constExprNode("12:00:00", null, ValueType.TIME, Span.of(0, 2)).closeExpr();;
    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitInfixExpr(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);

    var root = astBuilderWalker.getBuilder().build();
    assertNull(root.getValueType());

    verify(builder).constExprNode(eq("AAA"), isNull(), eq(ValueType.STRING), any(Span.class));
    verify(builder).constExprNode(eq("12:00:00"), isNull(), eq(ValueType.TIME), any(Span.class));
    verify(builder).callExprNode(eq(new NodeOperator("-", NodeOperator.Category.INFIX)), isNull(), any(Span.class));
    verify(builder).infixExprNode(eq("-"), any(Span.class));

    verify(builder, atLeastOnce()).getTopNode();
    verify(builder, atLeastOnce()).closeExpr();
    verify(builder).build();
    verify(errorLogger).logError(CompilerErrorCode.CANNOT_SUBTRACT_TYPES, new Object[]{ValueType.STRING, ValueType.TIME}, Span.of(0, 10));

    verifyNoMoreInteractions(builder, errorLogger);
  }


  @Test
  void exitInfixExprRule_shouldWarnLackOfArguments() {
    VariableFinder variableFinder = mock();
    DialobRuleParser.InfixExprContext ctx = mock();
    ErrorLogger errorLogger = mock();
    ctx.op = mock(Token.class);
    ctx.start = mock(Token.class);
    ctx.stop = mock(Token.class);

    when(ctx.op.getText()).thenReturn("+");
    when(ctx.getStart()).thenReturn(ctx.start);
    when(ctx.getStop()).thenReturn(ctx.stop);
    when(ctx.start.getStartIndex()).thenReturn(0);
    when(ctx.stop.getStopIndex()).thenReturn(10);

    var builder = spy(new ASTBuilder());

    ASTBuilderWalker astBuilderWalker = new ASTBuilderWalker(builder, variableFinder, new HashMap<>());
    astBuilderWalker.setErrorLogger(errorLogger);

    // First enter to create a node
    astBuilderWalker.enterInfixExpr(ctx);

    NodeBase nodeBeforeExit = astBuilderWalker.getBuilder().getTopNode();
    assertNotNull(nodeBeforeExit);

    // Exit should pop the builder
    astBuilderWalker.exitInfixExpr(ctx);
    NodeBase nodeAfterExit = astBuilderWalker.getBuilder().getTopNode();

    // The top node should be null after popping the only node
    assertNull(nodeAfterExit);

    verify(builder).callExprNode(eq(new NodeOperator("+", NodeOperator.Category.INFIX)), isNull(), any(Span.class));
    verify(builder).infixExprNode(eq("+"), any(Span.class));

    verify(builder, atLeastOnce()).getTopNode();
    verify(builder, atLeastOnce()).closeExpr();
    verify(errorLogger).logError(CompilerErrorCode.OPERATOR_REQUIRES_2_OPERANDS, new Object[]{"+"}, Span.of(0, 10));

    verifyNoMoreInteractions(errorLogger, builder);
  }


}
