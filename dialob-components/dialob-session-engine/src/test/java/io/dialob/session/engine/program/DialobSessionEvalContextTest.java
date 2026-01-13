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
package io.dialob.session.engine.program;

import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.session.engine.session.command.event.Event;
import io.dialob.session.engine.session.model.DialobSession;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import io.dialob.session.engine.session.model.ItemStates;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

class DialobSessionEvalContextTest {

  @Test
  void shouldVisitUpdatedItems() {
    FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
    EvalContext.SessionFacade dialobSession = mock();
    Consumer<Event> updatesConsumer = mock();

    ItemState originalState = mock(ItemState.class);
    ItemState updatedState = mock(ItemState.class);
    when(originalState.id()).thenReturn(IdUtils.toId("is1"));

    when(dialobSession.getItemStates()).thenReturn(new ItemStates.Builder()
        .putItemStates(IdUtils.toId("is1"), originalState)
        .build(),
      new ItemStates.Builder()
        .putItemStates(IdUtils.toId("is1"), updatedState)
        .build());

    DialobSessionEvalContext context = new DialobSessionEvalContext(functionRegistry, dialobSession, updatesConsumer, false, null);
    context.registerUpdate(updatedState,originalState);

    EvalContext.UpdatedItemsVisitor visitor = mock(EvalContext.UpdatedItemsVisitor.class);
    EvalContext.UpdatedItemsVisitor.UpdatedItemStateVisitor updatedItemStateVisitor = mock(EvalContext.UpdatedItemsVisitor.UpdatedItemStateVisitor.class);

    when(visitor.visitUpdatedItems()).thenReturn(Optional.of(updatedItemStateVisitor));
    context.accept(visitor);



    verify(originalState).id();

    InOrder order = inOrder(visitor, updatedItemStateVisitor);

    order.verify(visitor).start();
    order.verify(visitor).visitUpdatedItems();
    order.verify(updatedItemStateVisitor).visitUpdatedItemState(originalState, updatedState);
    order.verify(updatedItemStateVisitor).end();
    order.verify(visitor).visitUpdatedErrorStates();
    order.verify(visitor).visitUpdatedValueSets();
    order.verify(visitor).end();
    order.verifyNoMoreInteractions();

    verify(dialobSession, times(2)).getItemStates();

    verifyNoMoreInteractions(originalState, dialobSession);

  }

  @Test
  void shouldVisitCreatedItems() {
    FunctionRegistry functionRegistry = mock();
    EvalContext.SessionFacade dialobSession = mock();
    Consumer<Event> updatesConsumer = mock();

    ItemState originalState = null;
    ItemState updatedState = mock();

    when(updatedState.id()).thenReturn(IdUtils.toId("is1"));
    when(dialobSession.getItemStates()).thenReturn(new ItemStates.Builder()
        .build(),
      new ItemStates.Builder()
        .putItemStates(IdUtils.toId("is1"), updatedState)
        .build());

    DialobSessionEvalContext context = new DialobSessionEvalContext(functionRegistry, dialobSession, updatesConsumer, false, null);
    context.registerUpdate(updatedState,null);

    EvalContext.UpdatedItemsVisitor visitor = mock(EvalContext.UpdatedItemsVisitor.class);
    EvalContext.UpdatedItemsVisitor.UpdatedItemStateVisitor updatedItemStateVisitor = mock(EvalContext.UpdatedItemsVisitor.UpdatedItemStateVisitor.class);

    when(visitor.visitUpdatedItems()).thenReturn(Optional.of(updatedItemStateVisitor));
    context.accept(visitor);

    verify(updatedState).id();

    InOrder order = inOrder(visitor, updatedItemStateVisitor);

    order.verify(visitor).start();
    order.verify(visitor).visitUpdatedItems();
    order.verify(updatedItemStateVisitor).visitUpdatedItemState(null, updatedState);
    order.verify(updatedItemStateVisitor).end();
    order.verify(visitor).visitUpdatedErrorStates();
    order.verify(visitor).visitUpdatedValueSets();
    order.verify(visitor).end();
    order.verifyNoMoreInteractions();

    verify(dialobSession, times(2)).getItemStates();

    verifyNoMoreInteractions(dialobSession);

  }

  @Test
  void shouldVisitRemovedItems() {
    FunctionRegistry functionRegistry = mock(FunctionRegistry.class);
    EvalContext.SessionFacade dialobSession = mock(DialobSession.class);
    Consumer<Event> updatesConsumer = mock(Consumer.class);

    ItemState originalState = mock();
    ItemState updatedState = null;

    when(originalState.id()).thenReturn(IdUtils.toId("is1"));

    when(dialobSession.getItemStates()).thenReturn(new ItemStates.Builder()
        .putItemStates(IdUtils.toId("is1"), originalState)
        .build(),
      new ItemStates.Builder()
        .build());

    DialobSessionEvalContext context = new DialobSessionEvalContext(functionRegistry, dialobSession, updatesConsumer, false, null);
    context.registerUpdate(updatedState,originalState);

    EvalContext.UpdatedItemsVisitor visitor = mock();
    EvalContext.UpdatedItemsVisitor.UpdatedItemStateVisitor updatedItemStateVisitor = mock();

    when(visitor.visitUpdatedItems()).thenReturn(Optional.of(updatedItemStateVisitor));
    context.accept(visitor);



    verify(originalState).id();

    InOrder order = inOrder(visitor, updatedItemStateVisitor);

    order.verify(visitor).start();
    order.verify(visitor).visitUpdatedItems();
    order.verify(updatedItemStateVisitor).visitUpdatedItemState(originalState, null);
    order.verify(updatedItemStateVisitor).end();
    order.verify(visitor).visitUpdatedErrorStates();
    order.verify(visitor).visitUpdatedValueSets();
    order.verify(visitor).end();
    order.verifyNoMoreInteractions();

    verify(dialobSession, times(2)).getItemStates();

    verifyNoMoreInteractions(originalState, dialobSession);

  }

}
