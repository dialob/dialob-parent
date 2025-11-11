package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.api.ObjectValueType;
import io.dialob.rule.parser.api.ValueType;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
public final class ObjectExprNode extends NodeBase {
  ObjectExprNode(NodeBase parent, Span span) {
    super(parent, span);
  }

  private List<KeyValueExprNode> fields = new ArrayList<>();

  @Override
  public ObjectExprNode addSubnode(@NonNull NodeBase node) {
    node.setParent(this);
    if (node instanceof KeyValueExprNode keyValueExprNode) {
      fields.add(keyValueExprNode);
      return this;
    }
    throw new IllegalArgumentException("Only KeyValueExprNode nodes are allowed as subnodes of ObjectExprNode");
  }

  @Override
  public ValueType getValueType() {
    return ObjectValueType.objectOf(Collections.emptyMap());
  }

  @NonNull
  @Override
  public List<NodeBase> getSubnodes() {
    return (List<NodeBase>) (Object) fields;
  }

  @NonNull
  @Override
  public NodeOperator getNodeOperator() {
    return NodeOperator.OBJECT;
  }

  @Override
  public void setValueType(@NonNull ValueType type) {
    throw new UnsupportedOperationException("Object expression node value type is derived from its fields");
  }

  @Override
  public NodeBase accept(@NonNull ASTVisitor visitor) {
    ASTVisitor subvisitor = visitor.visitObjectExpr(this);
    if (subvisitor != null) {
      int i = 0;
      int j = 0;
      var nodes = fields.toArray(new KeyValueExprNode[0]);
      while (i < nodes.length) {
        KeyValueExprNode replaceNode = nodes[i++].accept(subvisitor);
        if (replaceNode != null) {
          replaceNode.setParent(this);
        }
        nodes[j++] = replaceNode;
      }
      if (j < nodes.length) {
        nodes = Arrays.copyOf(nodes, j);
      }
      fields = Arrays.asList(nodes);
    }
    return visitor.endObjectExpr(this);
  }

  @Override
  public String toString() {
    return "{" + fields.stream().map(Object::toString).collect(Collectors.joining(",")) + "}";
  }
}
