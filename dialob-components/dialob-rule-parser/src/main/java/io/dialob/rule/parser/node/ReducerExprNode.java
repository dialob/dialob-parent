package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true, of = {"nodeOperator", "arguments"})
public class ReducerExprNode extends CallExprNode {

  private static final long serialVersionUID = -5417814591284802033L;

  public ReducerExprNode(NodeBase parent, @NonNull NodeOperator nodeOperator, Span span) {
    super(parent, nodeOperator, null, span);
  }


}
