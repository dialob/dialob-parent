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

import io.dialob.session.engine.session.AsyncFunctionCall;
import io.dialob.session.engine.session.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;

class EvalResultTest {

  @Test
  void shouldVisitLanguageChange() {
    EvalResult evalResult = new EvalResult.Builder()
      .originalLanguage("en")
      .language("fi")
      .originalStates(ItemStates.EMPTY)
      .updatedStates(ItemStates.EMPTY)
      .updatedItemIds(List.of())
      .updatedErrorIds(List.of())
      .updatedValueSetIds(List.of())
      .pendingUpdates(Map.of())
      .didComplete(false)
      .build();

    EvalResult.UpdatedItemsVisitor visitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.class);
    EvalResult.UpdatedItemsVisitor.UpdatedSessionStateVisitor sessionVisitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.UpdatedSessionStateVisitor.class);
    when(visitor.visitSession()).thenReturn(Optional.of(sessionVisitor));

    evalResult.accept(visitor);

    verify(visitor).start();
    verify(visitor).visitSession();
    verify(sessionVisitor).visitLanguageChange("en", "fi");
    verify(sessionVisitor).end();
    verify(visitor).end();
  }

  @Test
  void shouldVisitUpdatedItems() {
    ItemId itemId = IdUtils.toId("item1");
    ItemState originalState = Mockito.mock(ItemState.class);
    ItemState updatedState = Mockito.mock(ItemState.class);

    EvalResult evalResult = new EvalResult.Builder()
      .originalStates(new ItemStates(Map.of(itemId, originalState), Map.of(), Map.of()))
      .updatedStates(new ItemStates(Map.of(itemId, updatedState), Map.of(), Map.of()))
      .updatedItemIds(List.of(itemId))
      .updatedErrorIds(List.of())
      .updatedValueSetIds(List.of())
      .pendingUpdates(Map.of())
      .didComplete(false)
      .build();

    EvalResult.UpdatedItemsVisitor visitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.class);
    EvalResult.UpdatedItemsVisitor.UpdatedItemStateVisitor itemVisitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.UpdatedItemStateVisitor.class);
    when(visitor.visitUpdatedItems()).thenReturn(Optional.of(itemVisitor));

    evalResult.accept(visitor);

    verify(visitor).start();
    verify(visitor).visitUpdatedItems();
    verify(itemVisitor).visitUpdatedItemState(originalState, updatedState);
    verify(itemVisitor).end();
    verify(visitor).end();
  }

  @Test
  void shouldVisitUpdatedErrorStates() {
    ItemId itemId = IdUtils.toId("item1");
    ErrorId errorId = new ErrorId(itemId, "err1");
    ErrorState originalState = Mockito.mock(ErrorState.class);
    ErrorState updatedState = Mockito.mock(ErrorState.class);

    EvalResult evalResult = new EvalResult.Builder()
      .originalStates(new ItemStates(Map.of(), Map.of(), Map.of(errorId, originalState)))
      .updatedStates(new ItemStates(Map.of(), Map.of(), Map.of(errorId, updatedState)))
      .updatedItemIds(List.of())
      .updatedErrorIds(List.of(errorId))
      .updatedValueSetIds(List.of())
      .pendingUpdates(Map.of())
      .didComplete(false)
      .build();

    EvalResult.UpdatedItemsVisitor visitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.class);
    EvalResult.UpdatedItemsVisitor.UpdatedErrorStateVisitor errorVisitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.UpdatedErrorStateVisitor.class);
    when(visitor.visitUpdatedErrorStates()).thenReturn(Optional.of(errorVisitor));

    evalResult.accept(visitor);

    verify(visitor).start();
    verify(visitor).visitUpdatedErrorStates();
    verify(errorVisitor).visitUpdatedErrorState(originalState, updatedState);
    verify(errorVisitor).end();
    verify(visitor).end();
  }

  @Test
  void shouldVisitUpdatedValueSets() {
    ValueSetId valueSetId = new ValueSetId("vs1");
    ValueSetState originalState = Mockito.mock(ValueSetState.class);
    ValueSetState updatedState = Mockito.mock(ValueSetState.class);

    EvalResult evalResult = new EvalResult.Builder()
      .originalStates(new ItemStates(Map.of(), Map.of(valueSetId, originalState), Map.of()))
      .updatedStates(new ItemStates(Map.of(), Map.of(valueSetId, updatedState), Map.of()))
      .updatedItemIds(List.of())
      .updatedErrorIds(List.of())
      .updatedValueSetIds(List.of(valueSetId))
      .pendingUpdates(Map.of())
      .didComplete(false)
      .build();

    EvalResult.UpdatedItemsVisitor visitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.class);
    EvalResult.UpdatedItemsVisitor.UpdatedValueSetVisitor valueSetVisitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.UpdatedValueSetVisitor.class);
    when(visitor.visitUpdatedValueSets()).thenReturn(Optional.of(valueSetVisitor));

    evalResult.accept(visitor);

    verify(visitor).start();
    verify(visitor).visitUpdatedValueSets();
    verify(valueSetVisitor).visitUpdatedValueSet(originalState, updatedState);
    verify(valueSetVisitor).end();
    verify(visitor).end();
  }

  @Test
  void shouldVisitAsyncFunctionCalls() {
    ItemId itemId = IdUtils.toId("item1");
    AsyncFunctionCall asyncFunctionCall = Mockito.mock(AsyncFunctionCall.class);

    EvalResult evalResult = new EvalResult.Builder()
      .originalStates(ItemStates.EMPTY)
      .updatedStates(ItemStates.EMPTY)
      .updatedItemIds(List.of())
      .updatedErrorIds(List.of())
      .updatedValueSetIds(List.of())
      .pendingUpdates(Map.of(itemId, asyncFunctionCall))
      .didComplete(false)
      .build();

    EvalResult.UpdatedItemsVisitor visitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.class);
    EvalResult.UpdatedItemsVisitor.AsyncFunctionCallVisitor asyncVisitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.AsyncFunctionCallVisitor.class);
    when(visitor.visitAsyncFunctionCalls()).thenReturn(Optional.of(asyncVisitor));

    evalResult.accept(visitor);

    verify(visitor).start();
    verify(visitor).visitAsyncFunctionCalls();
    verify(asyncVisitor).visitAsyncFunctionCall(asyncFunctionCall);
    verify(asyncVisitor).end();
    verify(visitor).end();
  }

  @Test
  void shouldVisitCompleted() {
    EvalResult evalResult = new EvalResult.Builder()
      .originalStates(ItemStates.EMPTY)
      .updatedStates(ItemStates.EMPTY)
      .updatedItemIds(List.of())
      .updatedErrorIds(List.of())
      .updatedValueSetIds(List.of())
      .pendingUpdates(Map.of())
      .didComplete(true)
      .build();

    EvalResult.UpdatedItemsVisitor visitor = Mockito.mock(EvalResult.UpdatedItemsVisitor.class);

    evalResult.accept(visitor);

    verify(visitor).start();
    verify(visitor).visitCompleted();
    verify(visitor).end();
  }
}
