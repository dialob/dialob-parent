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
package io.dialob.rule.parser;

import io.dialob.rule.parser.node.*;
import edu.umd.cs.findbugs.annotations.NonNull;

public class CloneVisitor implements ASTVisitor {

    private ASTBuilder builder;

    public CloneVisitor() {
        this(new ASTBuilder());
    }

    public CloneVisitor(ASTBuilder builder) {
        this.builder = builder;
    }



    @Override
    public ASTVisitor visitCallExpr(@NonNull CallExprNode node) {
        builder = builder.callExprNode(node);
        return this;
    }

    @NonNull
    @Override
    public NodeBase endCallExpr(@NonNull CallExprNode node) {
        builder = builder.closeExpr();
        return node;
    }

    @NonNull
    @Override
    public NodeBase visitConstExpr(@NonNull ConstExprNode node) {
        builder = builder.constExprNode(node).closeExpr();
        return node;
    }

    @NonNull
    @Override
    public NodeBase visitIdExpr(@NonNull IdExprNode node) {
        builder = builder.idExprNode(node).closeExpr();
        return node;
    }

    public ASTBuilder getASTBuilder() {
        return builder;
    }
}
