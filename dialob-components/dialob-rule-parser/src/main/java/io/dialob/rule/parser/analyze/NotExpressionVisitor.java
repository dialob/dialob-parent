/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
package io.dialob.rule.parser.analyze;

import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.node.*;
import edu.umd.cs.findbugs.annotations.NonNull;

public class NotExpressionVisitor implements ASTVisitor {

    @Override
    public ASTVisitor visitCallExpr(@NonNull CallExprNode node) {
        if (node.getNodeOperator().isNotOp()) {
            return null;
        }
        return this;
    }

    @NonNull
    @Override
    public NodeBase endCallExpr(@NonNull final CallExprNode node) {
        if (node.getNodeOperator().isRelation() || node.getNodeOperator().isLogical() || node.getNodeOperator().getCategory() == NodeOperator.Category.FUNCTION) {
            if (node.getNodeOperator().getOperator().equals("not")) {
                return node.getLhs();
            }
            final NodeOperator newOperator = node.getNodeOperator().not();
            final CallExprNode callExprNode = (CallExprNode)new ASTBuilder().callExprNode(
                    newOperator,
                    node.getValueType(),
                    node.getSpan()).closeExpr().build();
            node.getSubnodes().forEach(callExprNode::addSubnode);
            return callExprNode;
        }
        return node;
    }

    @NonNull
    @Override
    public NodeBase visitConstExpr(@NonNull ConstExprNode node) {
        if (node.getValueType() == ValueType.BOOLEAN) {
            Boolean value = (Boolean) node.getAsValueType();
            value = !value;
            return new ASTBuilder().constExprNode(value.toString(),null, ValueType.BOOLEAN, node.getSpan()).closeExpr().build();
        }
        return node;
    }

    @NonNull
    @Override
    public NodeBase visitIdExpr(@NonNull IdExprNode node) {
        if (node.getValueType() == ValueType.BOOLEAN) {
            return new ASTBuilder()
                    .callExprNode(NodeOperator.createNodeOperator("not"), ValueType.BOOLEAN, node.getSpan())
                        .idExprNode(node).closeExpr()
                    .closeExpr().build();
        }
        return node;
    }
}
