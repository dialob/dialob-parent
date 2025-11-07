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
package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ValueType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectExprNodeTest {

  @Test
  void shouldCreateObjectExprNode() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertNotNull(node);
    assertNull(node.getParent());
    assertEquals(Span.undefined(), node.getSpan());
  }

  @Test
  void shouldCreateObjectExprNodeWithParent() {
    CallExprNode parent = new CallExprNode(null, NodeOperator.createNodeOperator("+"), Span.undefined());
    ObjectExprNode node = new ObjectExprNode(parent, Span.of(0, 10));

    assertEquals(parent, node.getParent());
    assertEquals(Span.of(0, 10), node.getSpan());
  }

  @Test
  void shouldReturnObjectNodeOperator() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertEquals(NodeOperator.OBJECT, node.getNodeOperator());
  }

  @Test
  void shouldAcceptVisitorAndReturnSelf() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());
    ASTVisitor visitor = new ASTVisitor() {};

    NodeBase result = node.accept(visitor);

    assertEquals(node, result);
  }

  @Test
  void shouldAcceptVisitorThatReturnsNull() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());
    ASTVisitor visitor = new ASTVisitor() {
      @Override
      public ASTVisitor visitObjectExpr(@NonNull ObjectExprNode node) {
        return null;
      }
    };

    NodeBase result = node.accept(visitor);

    assertEquals(node, result);
  }

  @Test
  void shouldAcceptVisitorThatReturnsModifiedNode() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());
    ObjectExprNode replacement = new ObjectExprNode(null, Span.of(5, 15));

    ASTVisitor visitor = new ASTVisitor() {
      @Override
      public NodeBase endObjectExpr(@NonNull ObjectExprNode node) {
        return replacement;
      }
    };

    NodeBase result = node.accept(visitor);

    assertEquals(replacement, result);
  }

  @Test
  void shouldSetAndGetValueType() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertNull(node.getValueType());

    node.setValueType(ValueType.STRING);

    assertEquals(ValueType.STRING, node.getValueType());
  }

  @Test
  void shouldSetParent() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());
    CallExprNode parent = new CallExprNode(null, NodeOperator.createNodeOperator("+"), Span.undefined());

    node.setParent(parent);

    assertEquals(parent, node.getParent());
  }

  @Test
  void shouldReturnFalseForIsConstant() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertFalse(node.isConstant());
  }

  @Test
  void shouldReturnFalseForIsIdentifier() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertFalse(node.isIdentifier());
  }

  @Test
  void shouldReturnEmptyListForGetSubnodes() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertTrue(node.getSubnodes().isEmpty());
  }

  @Test
  void shouldThrowExceptionWhenAddingSubnode() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());
    KeyValueExprNode keyValue = new KeyValueExprNode(null, Span.undefined());

    assertThrows(IllegalStateException.class, () -> node.addSubnode(keyValue));
  }

  @Test
  void shouldBeEqualToSelf() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertEquals(node, node);
  }

  @Test
  void shouldNotBeEqualToNull() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertNotEquals(node, null);
  }

  @Test
  void shouldBeEqualWhenBothHaveNoValueType() {
    ObjectExprNode node1 = new ObjectExprNode(null, Span.undefined());
    ObjectExprNode node2 = new ObjectExprNode(null, Span.undefined());

    assertEquals(node1, node2);
  }

  @Test
  void shouldBeEqualWhenBothHaveSameValueType() {
    ObjectExprNode node1 = new ObjectExprNode(null, Span.undefined());
    node1.setValueType(ValueType.STRING);

    ObjectExprNode node2 = new ObjectExprNode(null, Span.undefined());
    node2.setValueType(ValueType.STRING);

    assertEquals(node1, node2);
  }

  @Test
  void shouldNotBeEqualWhenValueTypesAreDifferent() {
    ObjectExprNode node1 = new ObjectExprNode(null, Span.undefined());
    node1.setValueType(ValueType.STRING);

    ObjectExprNode node2 = new ObjectExprNode(null, Span.undefined());
    node2.setValueType(ValueType.INTEGER);

    assertNotEquals(node1, node2);
  }

  @Test
  void shouldNotBeEqualWhenOnlyOneHasValueType() {
    ObjectExprNode node1 = new ObjectExprNode(null, Span.undefined());
    node1.setValueType(ValueType.STRING);

    ObjectExprNode node2 = new ObjectExprNode(null, Span.undefined());

    assertNotEquals(node1, node2);
  }

  @Test
  void shouldBeEqualToDifferentNodeTypeWhenBothHaveNullValueType() {
    // NodeBase.equals() only compares ValueType, not the node class type
    ObjectExprNode node1 = new ObjectExprNode(null, Span.undefined());
    CallExprNode node2 = new CallExprNode(null, NodeOperator.createNodeOperator("+"), Span.undefined());

    assertEquals(node1, node2);
  }

  @Test
  void shouldNotBeEqualToDifferentNodeTypeWithDifferentValueTypes() {
    ObjectExprNode node1 = new ObjectExprNode(null, Span.undefined());
    node1.setValueType(ValueType.STRING);

    CallExprNode node2 = new CallExprNode(null, NodeOperator.createNodeOperator("+"), Span.undefined());
    node2.setValueType(ValueType.INTEGER);

    assertNotEquals(node1, node2);
  }

  @Test
  void shouldHaveSameHashCodeForEqualNodes() {
    ObjectExprNode node1 = new ObjectExprNode(null, Span.undefined());
    node1.setValueType(ValueType.STRING);

    ObjectExprNode node2 = new ObjectExprNode(null, Span.undefined());
    node2.setValueType(ValueType.STRING);

    assertEquals(node1.hashCode(), node2.hashCode());
  }

  @Test
  void shouldHaveZeroHashCodeWhenValueTypeIsNull() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    assertEquals(0, node.hashCode());
  }

  @Test
  void shouldReturnTypedStringWithoutValueType() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());

    String result = node.toTypedString();

    assertNotNull(result);
    assertTrue(result.contains("null"));
  }

  @Test
  void shouldReturnTypedStringWithValueType() {
    ObjectExprNode node = new ObjectExprNode(null, Span.undefined());
    node.setValueType(ValueType.STRING);

    String result = node.toTypedString();

    assertNotNull(result);
    assertTrue(result.contains("STRING"));
  }

  @Test
  void shouldBuildObjectExprNodeUsingASTBuilder() {
    ASTBuilder builder = new ASTBuilder();

    NodeBase result = builder
      .objectExprNode(Span.of(0, 5))
      .closeExpr()
      .build();

    assertInstanceOf(ObjectExprNode.class, result);
    assertEquals(NodeOperator.OBJECT, result.getNodeOperator());
  }

  @Test
  void shouldBuildNestedObjectExprNode() {
    ASTBuilder builder = new ASTBuilder();

    NodeBase result = builder
      .callExprNode("+", Span.of(0, 10))
      .objectExprNode(Span.of(1, 5))
      .closeExpr()
      .closeExpr()
      .build();

    assertInstanceOf(CallExprNode.class, result);
    CallExprNode callNode = (CallExprNode) result;
    assertEquals(1, callNode.getSubnodes().size());
    assertInstanceOf(ObjectExprNode.class, callNode.getSubnodes().get(0));
  }
}
