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
package io.dialob.session.engine.session.command;

import io.dialob.api.proto.Action;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemState;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static io.dialob.session.engine.session.model.IdUtils.toId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class NextPageTest {

  @Test
  void nextShouldNavigateToFirstActivePage() {
    NextPage nextPage = CommandFactory.nextPage();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState activePage = Mockito.mock(ItemState.class);

    enableNext(context);


    when(context.getItemState(toId("p1"))).thenReturn(Optional.of(activePage));
    when(context.getItemState(toId("p2"))).thenReturn(Optional.of(activePage));
    when(activePage.isActive()).thenReturn(false).thenReturn(true);
    when(context.getEventsConsumer()).thenReturn(event -> {});

    ItemState itemState = new ItemState(toId("q"), null, "questionnaire", null, true, null, null, null, null, null);
    itemState = itemState.update().setItems(Arrays.asList(toId("p1"), toId("p2"), toId("p3"))).get();
    itemState = nextPage.update(context, itemState);
    assertEquals(toId("p2"), itemState.getActivePage().get());
    verify(activePage, times(2)).isActive();
    verifyNoMoreInteractions(activePage);
  }

  private void enableNext(EvalContext context) {
    ItemState questionnaire = Mockito.mock(ItemState.class);
    when(questionnaire.getAllowedActions()).thenReturn(Set.of(Action.Type.NEXT));
    when(context.getItemState(IdUtils.QUESTIONNAIRE_ID)).thenReturn(Optional.of(questionnaire));
  }

  private void disableNext(EvalContext context) {
    ItemState questionnaire = Mockito.mock(ItemState.class);
    when(questionnaire.getAllowedActions()).thenReturn(Set.of());
    when(context.getItemState(IdUtils.QUESTIONNAIRE_ID)).thenReturn(Optional.of(questionnaire));
  }

  @Test
  void nextShouldNotNavigateIfOnLastPage() {
    NextPage nextPage = CommandFactory.nextPage();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState activePage = Mockito.mock(ItemState.class);

    enableNext(context);
    when(context.getEventsConsumer()).thenReturn(event -> {});
    ItemState itemState = new ItemState(toId("q"), null, "questionnaire", null, true, null, null, null, null, null);
    itemState = itemState.update()
      .setItems(Arrays.asList(toId("p1"), toId("p2"), toId("p3")))
      .setActivePage(toId("p3")).get();
    itemState = nextPage.update(context, itemState);
    assertEquals(toId("p3"), itemState.getActivePage().get());
    verifyNoMoreInteractions(activePage);
  }

  @Test
  void nextShouldNavigateToNextActivePage() {
    NextPage nextPage = CommandFactory.nextPage();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState activePage = Mockito.mock(ItemState.class);
    enableNext(context);

    when(context.getItemState(toId("p3"))).thenReturn(Optional.of(activePage));
    when(activePage.isActive()).thenReturn(true);

    when(context.getEventsConsumer()).thenReturn(event -> {});
    ItemState itemState = new ItemState(toId("q"), null, "questionnaire", null, true, null, null, null, null, null);
    itemState = itemState.update()
      .setItems(Arrays.asList(toId("p1"), toId("p2"), toId("p3")))
      .setActivePage(toId("p2")).get();
    itemState = nextPage.update(context, itemState);
    assertEquals(toId("p3"), itemState.getActivePage().get());
    verify(activePage, times(1)).isActive();
    verifyNoMoreInteractions(activePage);
  }


  @Test
  void nextNotShouldNavigateToNextActivePageWhenNextIsDisabled() {
    NextPage nextPage = CommandFactory.nextPage();
    EvalContext context = Mockito.mock(EvalContext.class);
    ItemState activePage = Mockito.mock(ItemState.class);
    disableNext(context);

    when(context.getItemState(toId("p3"))).thenReturn(Optional.of(activePage));
    when(activePage.isActive()).thenReturn(true);

    when(context.getEventsConsumer()).thenReturn(event -> {});
    ItemState itemState = new ItemState(toId("q"), null, "questionnaire", null, true, null, null, null, null, null);
    itemState = itemState.update()
      .setItems(Arrays.asList(toId("p1"), toId("p2"), toId("p3")))
      .setActivePage(toId("p2")).get();
    itemState = nextPage.update(context, itemState);
    assertEquals(toId("p2"), itemState.getActivePage().get());
    verifyNoMoreInteractions(activePage);
  }

}
