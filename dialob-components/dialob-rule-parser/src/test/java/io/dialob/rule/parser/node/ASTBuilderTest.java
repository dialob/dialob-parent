package io.dialob.rule.parser.node;

import io.dialob.rule.parser.api.ValueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ASTBuilderTest {


  @Test
  void shouldThrowIllegalStateExceptionIfBuildCalledTooEarl() {
    ASTBuilder astBuilder = new ASTBuilder();
    astBuilder.idExprNode(null, "x", ValueType.BOOLEAN, ImmutableSpan.of(0,1));
    Assertions.assertThrows(IllegalStateException.class, () -> astBuilder.build());
  }


  @Test
  void shouldCreateIdNode() {
    ASTBuilder astBuilder = new ASTBuilder();
    astBuilder.idExprNode(null, "x", ValueType.BOOLEAN, ImmutableSpan.of(0,1)).closeExpr();
    NodeBase nodeBase = astBuilder.build();

    assertEquals("x", nodeBase.toString());
    assertEquals(ValueType.BOOLEAN, nodeBase.getValueType());
    assertEquals("$id", nodeBase.getNodeOperator().getOperator());
    assertEquals(1, nodeBase.getAllDependencies().size());
    assertEquals(ValueType.BOOLEAN, nodeBase.getAllDependencies().get("x"));
    assertNull(nodeBase.getParent());
  }

  @Test
  void shouldCreateCallNode() {
    ASTBuilder astBuilder = new ASTBuilder();

    astBuilder.callExprNode("func", ValueType.BOOLEAN, ImmutableSpan.of(0,4)).closeExpr();
    NodeBase nodeBase = astBuilder.build();

    assertEquals("(func)", nodeBase.toString());
    assertEquals(ValueType.BOOLEAN, nodeBase.getValueType());
    assertEquals("func", nodeBase.getNodeOperator().getOperator());
    assertEquals(0, nodeBase.getAllDependencies().size());
    assertNull(nodeBase.getAllDependencies().get("func"));
    assertNull(nodeBase.getParent());
  }

}

