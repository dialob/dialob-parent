package io.dialob.rule.parser.node;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;


public interface ASTVisitor {

  @Nullable
  default ASTVisitor visitCallExpr(@NonNull CallExprNode node) {
    return this;
  }

  @NonNull
  default NodeBase endCallExpr(@NonNull CallExprNode node) {
    return node;
  }

  @NonNull
  default NodeBase visitConstExpr(@NonNull ConstExprNode node) {
    return node;
  }

  @NonNull
  default NodeBase visitIdExpr(@NonNull IdExprNode node) {
    return node;
  }

  @NonNull
  default ASTVisitor visitKeyValueExpr(@NonNull KeyValueExprNode node) {
    return this;
  }

  @NonNull
  default KeyValueExprNode endKeyValueExpr(@NonNull KeyValueExprNode node) {
    return node;
  }

  @Nullable
  default ASTVisitor visitObjectExpr(@NonNull ObjectExprNode node) {
    return this;
  }

  @NonNull
  default NodeBase endObjectExpr(@NonNull ObjectExprNode node) {
    return node;
  }

}
