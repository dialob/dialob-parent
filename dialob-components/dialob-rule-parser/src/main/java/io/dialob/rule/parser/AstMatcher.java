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
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.node.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class AstMatcher implements ASTVisitor {

  private final List<Pair<Predicate<NodeBase>, UnaryOperator<NodeBase>>> matchers = new ArrayList<>();

  public NodeBase onVisitEnd(NodeBase baseNode) {
    return baseNode;
  }

  public void whenMatches(Predicate<NodeBase> matcher, UnaryOperator<NodeBase> thenAction) {
    matchers.add(new ImmutablePair<>(matcher, thenAction));
  }

  @Override
  public ASTVisitor visitCallExpr(@NonNull CallExprNode node) {
    checkMatchingBefore(node);
    return this;
  }


  @Override
  @NonNull
  public NodeBase endCallExpr(@NonNull CallExprNode node) {
    NodeBase nodeBase = checkMatching(node);
    if (nodeBase.getParent() == null) {
      // Last visit
      return onVisitEnd(nodeBase);
    }
    return nodeBase;
  }

  protected void checkIsThisLast(NodeBase ignoreNodeBase) {
    // placeholder
  }

  @Override
  @NonNull
  public NodeBase visitConstExpr(@NonNull ConstExprNode node) {
    NodeBase nodeBase = checkMatching(node);
    checkIsThisLast(node);
    return nodeBase;
  }

  @Override
  @NonNull
  public NodeBase visitIdExpr(@NonNull IdExprNode node) {
    NodeBase nodeBase = checkMatching(node);
    checkIsThisLast(node);
    return nodeBase;
  }

  protected void checkMatchingBefore(final NodeBase ignoredNode) {
    // placeholder
  }

  @NonNull
  protected NodeBase checkMatching(final NodeBase node) {
    for (final Pair<Predicate<NodeBase>, UnaryOperator<NodeBase>> pair : matchers) {
      if (pair.getKey().test(node)) {
        return pair.getValue().apply(node);
      }
    }
    return node;
  }

  public static <T> Predicate<T> isNull() {
    return Objects::isNull;
  }

  public static <T> Predicate<T> is(@NonNull T equalsTo) {
    return object -> equalsTo == object || equalsTo.equals(object);
  }

  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return object -> !predicate.test(object);
  }


  @SafeVarargs
  public static <T> Predicate<T> or(Predicate<T>... predicates) {
    return object -> {
      for (Predicate<T> predicate : predicates) {
        if (predicate.test(object)) {
          return true;
        }
      }
      return false;
    };
  }

  public static Predicate<NodeBase> stringValue(Predicate<Object> predicate) {
    return node -> node instanceof ConstExprNode cen && predicate.test(cen.getValue());
  }

  public static Predicate<NodeBase> value(Predicate<Object> predicate) {
    return node -> node instanceof ConstExprNode cen && predicate.test(cen.getAsValueType());
  }

  public static Predicate<NodeBase> valueType(Predicate<ValueType> predicate) {
    return node -> predicate.test(node.getValueType());
  }

  public static Predicate<NodeBase> parent(Predicate<NodeBase> predicate) {
    return node -> predicate.test(node.getParent());
  }

  public static Predicate<NodeBase> lhs(Predicate<NodeBase> predicate) {
    return node -> node instanceof CallExprNode cen && predicate.test(cen.getLhs());
  }

  public static Predicate<NodeBase> rhs(Predicate<NodeBase> predicate) {
    return node -> node instanceof CallExprNode cen && predicate.test(cen.getRhs());
  }

  public static Predicate<NodeBase> args(Predicate<List<NodeBase>> predicate) {
    return node -> node instanceof CallExprNode && predicate.test(node.getSubnodes());
  }

  public static Predicate<List<NodeBase>> length(Predicate<Integer> predicate) {
    return args -> args != null && predicate.test(args.size());
  }

  public static Predicate<List<NodeBase>> anyMatches(Predicate<NodeBase> predicate) {
    return args -> {
      if (args.isEmpty()) {
        return false;
      }
      for (NodeBase arg : args) {
        if (predicate.test(arg)) {
          return true;
        }
      }
      return false;
    };
  }

  public static Predicate<List<NodeBase>> allMatches(Predicate<NodeBase> predicate) {
    return args -> {
      if (args.isEmpty()) {
        return false;
      }
      for (NodeBase arg : args) {
        if (!predicate.test(arg)) {
          return false;
        }
      }
      return true;
    };
  }


  public static Predicate<NodeBase> operCategory(Predicate<NodeOperator.Category> predicate) {
    return node -> node instanceof CallExprNode && predicate.test(node.getNodeOperator().getCategory());
  }

  public static Predicate<NodeBase> operator(Predicate<String> predicate) {
    return node -> node instanceof CallExprNode && predicate.test(node.getNodeOperator().getOperator());
  }

  public static Predicate<NodeBase> callNode() {
    return callNode(any());
  }

  public static Predicate<NodeBase> callNode(@NonNull Predicate<NodeBase> predicate) {
    return node -> node instanceof CallExprNode && predicate.test(node);
  }

  public static Predicate<NodeBase> idNode() {
    return idNode(any());
  }

  public static Predicate<NodeBase> idNode(@NonNull Predicate<NodeBase> predicate) {
    return node -> node instanceof IdExprNode && predicate.test(node);
  }

  public static <T> Predicate<T> any() {
    return t -> true;
  }

  public static Predicate<NodeBase> constNode() {
    return constNode(any());
  }

  public static Predicate<NodeBase> constNode(@NonNull Predicate<NodeBase> predicate) {
    return node -> node instanceof ConstExprNode && predicate.test(node);
  }

  public ASTBuilder newASTBuilder() {
    return new ASTBuilder();
  }
}
