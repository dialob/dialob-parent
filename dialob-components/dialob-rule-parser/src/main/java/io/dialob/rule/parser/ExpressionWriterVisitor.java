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
package io.dialob.rule.parser;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.rule.parser.node.*;

public class ExpressionWriterVisitor implements ASTVisitor {

  private final String separator;

  private final NodeOperator nodeOperator;

  private StringBuilder stringBuilder = new StringBuilder();

  private ExpressionWriterVisitor subVisitor;

  private boolean first = true;

  ExpressionWriterVisitor() {
    this(null, "");
  }

  private ExpressionWriterVisitor(NodeOperator nodeOperator, String separator) {
    this.separator = separator;
    this.nodeOperator = nodeOperator;
  }

  @Override
  @NonNull
  public ASTVisitor visitCallExpr(@NonNull CallExprNode node) {
    addSeparator();
    String separator = getOperatorSeparator(node.getNodeOperator());

    subVisitor = createSubVisitor(node, separator);
    return subVisitor;
  }

  private String getOperatorSeparator(final NodeOperator nodeOperator) {
    String separator = "";
    final String operator = convertOperator(nodeOperator.getOperator());
    switch (nodeOperator.getCategory()) {
      case FUNCTION:
        stringBuilder.append(operator).append("(");
        separator = ",";
        break;
      case LOGICAL:
        if (nodeOperator.isUnary()) {
          stringBuilder.append(operator).append(" ");
          separator = "";
        } else {
          separator = " " + operator + " ";
        }
        break;
      case INFIX:
        separator = operator;
        break;
      case UNARY:
        separator = "";
        break;
      case RELATION:
        separator = " " + operator + " ";
        break;
      default:
    }
    return separator;
  }

  private String convertOperator(@NonNull String operator) {
    return operator;
  }

  private ExpressionWriterVisitor createSubVisitor(CallExprNode node, String separator) {
    return new ExpressionWriterVisitor(node.getNodeOperator(), separator);
  }

  @Override
  @NonNull
  public NodeBase endCallExpr(@NonNull CallExprNode node) {
    switch (node.getNodeOperator().getCategory()) {
      case FUNCTION:
        stringBuilder.append(subVisitor.toString()).append(")");
        break;
      case LOGICAL, INFIX:
        stringBuilder.append(addBrackets(subVisitor));
        break;
      case UNARY:
        stringBuilder.append(convertUnaryOper(node)).append(addBrackets(subVisitor));
        break;
      case RELATION:
        stringBuilder.append(subVisitor.toString());
        break;
      default:
    }
    return node;
  }

  private String convertUnaryOper(CallExprNode node) {
    return switch (node.getNodeOperator().getOperator()) {
      case "neg" -> "-";
      case "inv" -> "1/";
      default -> throw new IllegalStateException("Unknown unary operator " + node.getNodeOperator().getOperator());
    };
  }

  private String addBrackets(ExpressionWriterVisitor subVisitor) {
    if (subVisitor.nodeOperator != null && nodeOperator != null) {
      if (subVisitor.nodeOperator.getCategory() == nodeOperator.getCategory() &&
        subVisitor.nodeOperator.getPrecedenceWeight() < nodeOperator.getPrecedenceWeight()) {
        return "(" + subVisitor.stringBuilder.toString() + ")";
      }
      if (nodeOperator.isUnary()) {
        return "(" + subVisitor.stringBuilder.toString() + ")";
      }
    }
    return subVisitor.stringBuilder.toString();
  }


  @Override
  @NonNull
  public NodeBase visitConstExpr(@NonNull ConstExprNode node) {
    addSeparator();
    stringBuilder.append(node.getValue());
    if (node.getUnit() != null) {
      stringBuilder.append(node.getUnit());
    }
    return node;
  }

  private void addSeparator() {
    if (!first) {
      stringBuilder.append(separator);
    }
    first = false;
  }

  private String convertId(String id) {
    return id;
  }

  @Override
  @NonNull
  public NodeBase visitIdExpr(@NonNull IdExprNode node) {
    addSeparator();
    stringBuilder.append(convertId(node.getId()));
    return node;
  }

  @Override
  public String toString() {
    return stringBuilder.toString();
  }
}
