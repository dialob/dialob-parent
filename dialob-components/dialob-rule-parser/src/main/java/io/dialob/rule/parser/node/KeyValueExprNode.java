package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;

public class KeyValueExprNode extends NodeBase {
  protected KeyValueExprNode(NodeBase parent, Span span) {
    super(parent, span);
  }

  @NonNull
  @Override
  public NodeOperator getNodeOperator() {
    return NodeOperator.KEY_VALUE;
  }

  @Override
  public KeyValueExprNode accept(@NonNull ASTVisitor visitor) {
    return this;
  }
}
