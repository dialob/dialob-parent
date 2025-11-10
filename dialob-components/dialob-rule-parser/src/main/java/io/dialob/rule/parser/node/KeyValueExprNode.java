package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import lombok.Getter;
import lombok.Setter;

public class KeyValueExprNode extends NodeBase {

  @Getter
  @Setter
  private String key;

  @Getter
  private NodeBase value;

  protected KeyValueExprNode(NodeBase parent, Span span) {
    super(parent, span);
  }


  @NonNull
  @Override
  public NodeOperator getNodeOperator() {
    return NodeOperator.KEY_VALUE;
  }

  public NodeBase addSubnode(@NonNull NodeBase topNode) {
    if (this.value != null) {
      throw new IllegalStateException("KeyValueExprNode can have only one value node");
    }
    this.value = topNode;
    topNode.setParent(this);
    return this;
  }


  @Override
  public KeyValueExprNode accept(@NonNull ASTVisitor visitor) {
    var subvisitor = visitor.visitKeyValueExpr(this);
    if (value != null) {
      value.accept(subvisitor);
    }
    return visitor.endKeyValueExpr(this);
  }

  @Override
  public String toString() {
    return key + ":" + value;
  }
}
