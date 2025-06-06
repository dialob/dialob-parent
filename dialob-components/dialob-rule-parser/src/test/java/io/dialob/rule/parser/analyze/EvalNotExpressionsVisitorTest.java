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
package io.dialob.rule.parser.analyze;

import io.dialob.rule.parser.Expression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvalNotExpressionsVisitorTest {

    @Test
    void test() {
        assertExpressionNot("(and (> a 0) (or false true))", "a > 0 and not not (false or true)");
        assertExpressionNot("(and (> a 0) (and true false))", "a > 0 and not (false or true)");
        assertExpressionNot("(and (> a 0) (and true true))", "a > 0 and not (false or not true)");
        assertExpressionNot("(isNotAnswered a)", "not (a is answered)");
        assertExpressionNot("(isAnswered a)", "not (a is not answered)");
    }


    protected void assertExpressionNot(String expect, String expr) {
        Expression expression = Expression.createExpression(expr);
        expression.accept(new EvalNotExpressionsVisitor());
        assertEquals(expect,expression.getAst().toString());
    }


}
