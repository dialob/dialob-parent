package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.*;

public class ObjectExprNode extends NodeBase {
  protected ObjectExprNode(NodeBase parent, Span span) {
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
}
