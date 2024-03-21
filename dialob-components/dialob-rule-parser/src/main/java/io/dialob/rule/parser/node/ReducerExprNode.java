package io.dialob.rule.parser.node;

import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(callSuper = true, of = {"nodeOperator", "arguments"})
public class ReducerExprNode extends CallExprNode {

  private static final long serialVersionUID = -5417814591284802033L;

  public ReducerExprNode(NodeBase parent, @NotNull NodeOperator nodeOperator, Span span) {
    super(parent, nodeOperator, null, span);
  }


}
