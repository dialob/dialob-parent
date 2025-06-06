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
package io.dialob.session.engine.session;

import io.dialob.api.proto.ActionsFactory;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.program.DialobProgram;
import io.dialob.session.engine.program.DialobSessionEvalContextFactory;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.program.ProgramBuilder;
import io.dialob.session.engine.program.expr.arith.*;
import io.dialob.session.engine.program.model.ImmutableFormItem;
import io.dialob.session.engine.program.model.ImmutableGroup;
import io.dialob.session.engine.program.model.Program;
import io.dialob.session.engine.session.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class CreateDialobSessionProgramVisitorTest {

  @Test
  void prototypeItemsAreCollectedToPrototypes() {
    CreateDialobSessionProgramVisitor createDialobSessionProgramVisitor = new CreateDialobSessionProgramVisitor("tenant",
      "session", "en",
      null,
      (id, item) -> Optional.empty(), valueSetId -> Collections.emptyList(), new HashMap(), null, null, null);
    Program program = Mockito.mock(Program.class);
    createDialobSessionProgramVisitor.startProgram(program);
    createDialobSessionProgramVisitor.visitItems().ifPresent(itemVisitor -> {
      itemVisitor.visitItem(ImmutableFormItem.builder()
        .id(IdUtils.toId("proto"))
        .type("note")
        .isPrototype(true)
        .build());
    });
    createDialobSessionProgramVisitor.end();
    DialobSession dialobSession = createDialobSessionProgramVisitor.getDialobSession();
    assertTrue(dialobSession.findPrototype(IdUtils.toId("proto")).isPresent());

  }


  @Test
  void prototshouldCreateGroupForRowGroupAndPrototypeGroupForRows() {
    CreateDialobSessionProgramVisitor createDialobSessionProgramVisitor = new CreateDialobSessionProgramVisitor("tenant",
      "session", "en",
      null,
      (id, item) -> Optional.empty(), valueSetId -> Collections.emptyList(), new HashMap<>(), null, null, null);
    Program program = Mockito.mock(Program.class);
    createDialobSessionProgramVisitor.startProgram(program);
    createDialobSessionProgramVisitor.visitItems().ifPresent(itemVisitor -> {
      itemVisitor.visitItem(ImmutableGroup.builder()
        .id(IdUtils.toId("rg"))
        .type("rowgroup")
        .isPrototype(false)
        .itemsExpression(ImmutableConstant.builder().valueType(ValueType.arrayOf(ValueType.STRING)).value(Arrays.asList("a", "b")).build())
        .build());
      itemVisitor.visitItem(ImmutableGroup.builder()
        .id(IdUtils.toId("rg.*"))
        .type("row")
        .isPrototype(true)
        .itemsExpression(ImmutableRowItemsExpression.builder().itemIds(List.of(IdUtils.toId("rg.*.q1"))).build()).build());
    });
    createDialobSessionProgramVisitor.end();
    DialobSession dialobSession = createDialobSessionProgramVisitor.getDialobSession();
    assertTrue(dialobSession.getItemState(Operators.ref("rg")).isPresent());
    assertTrue(dialobSession.findPrototype(IdUtils.toId("rg.*")).isPresent());
  }

  @Test
  void shouldGetAllItemsById() {
    FunctionRegistry functionRegistry = Mockito.mock(FunctionRegistry.class);
    CreateDialobSessionProgramVisitor createDialobSessionProgramVisitor = new CreateDialobSessionProgramVisitor(
      "tenant",
      "session",
      "en",
      null,
      (id, item) -> Optional.empty(),
      valueSetId -> Collections.emptyList(),
      new HashMap<>(),
      null,
      null,
      null);
    Program program = new ProgramBuilder(functionRegistry)
      .startProgram()
        .setId("test")
        .addRoot().addItem("rg").build()
        .addRowGroup("rg").addItem("q1").addItem("q2").addItem("q3").build()
        .addQuestion("q1").setType("number").build()
        .addQuestion("q2").setType("decimal").build()
        .addQuestion("q3").setType("number").build()
      .build();
    DialobProgram dialobProgram = DialobProgram.createDialobProgram(program);

    // TODO session is created based on Program, but execution is done against DialobProgram??
    program.accept(createDialobSessionProgramVisitor);
    DialobSession dialobSession = createDialobSessionProgramVisitor.getDialobSession();
    DialobSessionEvalContextFactory contextFactory = new DialobSessionEvalContextFactory(functionRegistry, null);
    ActiveDialobSessionUpdater sessionUpdater = (ActiveDialobSessionUpdater) contextFactory.createSessionUpdater(dialobProgram, dialobSession, true);
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(List.of(ActionsFactory.addRow("rg"))));
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(List.of(ActionsFactory.addRow("rg"))));
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(List.of(ActionsFactory.addRow("rg"))));

    sessionUpdater = (ActiveDialobSessionUpdater) contextFactory.createSessionUpdater(dialobProgram, dialobSession, false);
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(List.of(ActionsFactory.answer("rg.0.q1", 1))));
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(List.of(ActionsFactory.answer("rg.0.q2", 1))));
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(List.of(ActionsFactory.answer("rg.2.q1", 2.0))));
    sessionUpdater.applyCommands(ActionToCommandMapper.toCommands(List.of(ActionsFactory.answer("rg.2.q2", 2.0))));

    assertTrue(dialobSession.getItemState(Operators.ref("rg.0")).isPresent());
    assertTrue(dialobSession.getItemState(Operators.ref("rg")).isPresent());
    assertTrue(dialobSession.findPrototype(IdUtils.toId("rg.*")).isPresent());



    assertEquals(BigInteger.ONE, dialobSession.getItemState(IdUtils.toId("rg.0.q1")).get().getValue());
    assertEquals(BigDecimal.valueOf(1.0), dialobSession.getItemState(IdUtils.toId("rg.0.q2")).get().getValue());

    EvalContext context = sessionUpdater.createEvalContext();

//    ItemId test = context.mapTo(IdUtils.toId("q2"), true);
    Optional<ItemState> test = context.findPrototype(IdUtils.toId("q2"));

    assertEquals(BigDecimal.valueOf(3.0), ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.DECIMAL_SUM,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q2"),
        ValueType.DECIMAL)
    ).eval(context));
    assertEquals(BigInteger.valueOf(3), ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.INTEGER_SUM,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q1"),
        ValueType.INTEGER)
    ).eval(context));
    assertNull(ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.INTEGER_SUM,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q3"),
        ValueType.INTEGER)
    ).eval(context));
    assertEquals(BigDecimal.valueOf(1.0), ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.DECIMAL_MIN,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q2"),
        ValueType.DECIMAL)
    ).eval(context));
    assertEquals(BigInteger.valueOf(1), ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.INTEGER_MIN,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q1"),
        ValueType.INTEGER)
    ).eval(context));
    assertNull(ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.INTEGER_MIN,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q3"),
        ValueType.INTEGER)
    ).eval(context));
    assertEquals(BigDecimal.valueOf(2.0), ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.DECIMAL_MAX,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q2"),
        ValueType.DECIMAL)
    ).eval(context));
    assertEquals(BigInteger.valueOf(2), ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.INTEGER_MAX,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q1"),
        ValueType.INTEGER)
    ).eval(context));
    assertNull(ImmutableArrayReducerOperator.of(
      ArrayReducerOperator.INTEGER_MAX,
      ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q3"),
        ValueType.INTEGER)
    ).eval(context));
    assertEquals(BigInteger.valueOf(2), ImmutableArrayReducerOperator.builder()
      .reducer(ArrayReducerOperator.ANSWER_COUNT)
      .arrayExpression(ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q1"), ValueType.INTEGER))
      .placeholderValue(BigInteger.ZERO)
      .build().eval(context));
    assertEquals(BigInteger.valueOf(0), ImmutableArrayReducerOperator.builder()
      .reducer(ArrayReducerOperator.ANSWER_COUNT)
      .arrayExpression(ImmutableCollectRowFieldsOperator.of(IdUtils.toId("rg.*.q3"), ValueType.INTEGER))
      .placeholderValue(BigInteger.ZERO)
      .build().eval(context));
  }


  @Test
  void shouldCreateI() {
    CreateDialobSessionProgramVisitor.InitialValueResolver initialValueResolver = Mockito.mock(CreateDialobSessionProgramVisitor.InitialValueResolver.class);

    Program program = Mockito.mock(Program.class);
    final ItemId itemId = IdUtils.toId("rg");

    final ImmutableGroup rowgroup = ImmutableGroup.builder()
      .id(itemId)
      .type("rowgroup")
      .isPrototype(false)
      .itemsExpression(ImmutableConstant.builder().valueType(ValueType.arrayOf(ValueType.STRING)).value(Arrays.asList("a", "b")).build())
      .valueType(ValueType.arrayOf(ValueType.INTEGER))
      .build();

    final ImmutableGroup row = ImmutableGroup.builder()
      .id(ImmutableItemIdPartial.of(Optional.of(itemId)))
      .type("row")
      .isPrototype(true)
      .itemsExpression(ImmutableRowItemsExpression.builder().itemIds(List.of(IdUtils.toId("rg.*.q1"))).build())
      .build();


    Mockito.when(initialValueResolver.apply(itemId, rowgroup)).thenReturn(Optional.of(Arrays.asList(BigInteger.ONE,BigInteger.TWO,BigInteger.valueOf(3))));


    CreateDialobSessionProgramVisitor createDialobSessionProgramVisitor = new CreateDialobSessionProgramVisitor("tenant",
      "session", "en",
      null,
      initialValueResolver, valueSetId -> Collections.emptyList(), new HashMap<>(), null, null, null);

    createDialobSessionProgramVisitor.startProgram(program);
    createDialobSessionProgramVisitor.visitItems().ifPresent(itemVisitor -> {
      itemVisitor.visitItem(rowgroup);
      itemVisitor.visitItem(row);
      itemVisitor.end();

    });
    createDialobSessionProgramVisitor.end();
    DialobSession dialobSession = createDialobSessionProgramVisitor.getDialobSession();
    final ItemState rg = dialobSession.getItemState(Operators.ref("rg")).get();
    assertEquals(Arrays.asList(BigInteger.ONE,BigInteger.TWO,BigInteger.valueOf(3)), rg.getValue());
    assertTrue(dialobSession.findPrototype(IdUtils.toId("rg.*")).isPresent());

    verify(initialValueResolver).apply(itemId, rowgroup);
    verifyNoMoreInteractions(initialValueResolver);
  }

}
