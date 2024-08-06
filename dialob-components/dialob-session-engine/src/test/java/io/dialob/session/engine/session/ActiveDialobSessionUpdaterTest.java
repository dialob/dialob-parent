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
package io.dialob.session.engine.session;

import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionsFactory;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.expr.arith.ImmutableConstant;
import io.dialob.session.engine.program.expr.arith.ImmutableContextVariableReference;
import io.dialob.session.engine.program.model.ImmutableGroup;
import io.dialob.session.engine.program.model.ImmutableProgram;
import io.dialob.session.engine.program.model.ImmutableVariableItem;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;

class ActiveDialobSessionUpdaterTest {

  @Test
  public void shouldSetContextVariable() {
    FunctionRegistry functionRegistry = mock(FunctionRegistry.class);

    DialobSessionEvalContextFactory contextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    DialobProgram program = DialobProgram.createDialobProgram(ImmutableProgram.builder()
      .id("p1")
      .addItems(ImmutableVariableItem.builder()
        .id(IdUtils.toId("c1"))
        .isPrototype(false)
        .type("context")
        .valueExpression(ImmutableContextVariableReference.builder().itemId(IdUtils.toId("c1")).valueType(ValueType.STRING).build())
        .build())
      .rootItem(ImmutableGroup.builder().id(IdUtils.QUESTIONNAIRE_ID).type("questionnaire").itemsExpression(ImmutableConstant.builder().valueType(ValueType.STRING).value(emptyList()).build()).build())
      .build());

    //String tenantId, final String sessionId, final String language, String activePage
    DialobSession session = program.createSession(contextFactory, "t1", "s1", "fi", "p1");

    Optional<ItemState> itemState;
    itemState = session.getItemState(IdUtils.toId("c1"));
    Assertions.assertNull(itemState.get().getValue());

    final Action setValue = ActionsFactory.setValue("c1", "new value");
    DialobSessionUpdater updater = new ActiveDialobSessionUpdater(contextFactory, program, session);
    updater.dispatchActions(Collections.singletonList(setValue), true);


    itemState = session.getItemState(IdUtils.toId("c1"));
    Assertions.assertEquals("new value", itemState.get().getValue());

  }
}
