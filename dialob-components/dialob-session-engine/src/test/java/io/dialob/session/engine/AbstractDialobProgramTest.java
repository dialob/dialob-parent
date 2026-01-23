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
package io.dialob.session.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.proto.Action;
import io.dialob.api.proto.ActionsFactory;
import io.dialob.session.engine.session.model.*;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

public abstract class AbstractDialobProgramTest {

  ObjectMapper objectMapper = new ObjectMapper().registerModules(new JavaTimeModule());

  public void assertInactive(DialobSession dialobSession, ItemId itemId) {
    assertFalse(dialobSession.getItemState(itemId).get().isActive());
  }

  public void assertActive(DialobSession dialobSession, ItemId itemId) {
    assertTrue(dialobSession.getItemState(itemId).get().isActive());
  }

  public void assertEnabled(DialobSession dialobSession, ItemId itemId) {
    assertFalse(dialobSession.getItemState(itemId).get().isDisabled());
  }

  public void assertDisabled(DialobSession dialobSession, ItemId itemId) {
    assertTrue(dialobSession.getItemState(itemId).get().isDisabled());
  }

  public Collection<Action> answer(ItemId questionId, String answer) {
    Action action = new Action.Builder()
      .type(Action.Type.ANSWER)
      .id(IdUtils.toString(questionId))
      .answer(answer).build();
    return Collections.singletonList(action);
  }

  public Collection<Action> setLocale(String locale) {
    return Collections.singletonList(ActionsFactory.setLocale(locale));
  }

  public Collection<Action> addRow(ItemId itemId) {
    Action action = new Action.Builder()
      .type(Action.Type.ADD_ROW)
      .id(IdUtils.toString(itemId))
      .build();
    return Collections.singletonList(action);
  }

  public Collection<Action> setValue(ItemId variableId, Object value) {
    Action action = new Action.Builder()
      .type(Action.Type.SET_VALUE)
      .id(IdUtils.toString(variableId))
      .value(value).build();
    return Collections.singletonList(action);
  }

  protected Collection<Action> gotoPage(String page) {
    Action action = new Action.Builder()
      .type(Action.Type.GOTO)
      .id(page).build();
    return Collections.singletonList(action);
  }

  protected Collection<Action> nextPage() {
    Action action = new Action.Builder()
      .type(Action.Type.NEXT).build();
    return Collections.singletonList(action);
  }

  protected Collection<Action> previousPage() {
    Action action = new Action.Builder()
      .type(Action.Type.PREVIOUS).build();
    return Collections.singletonList(action);
  }

  public ItemState anyItem() {
    return any(ItemState.class);
  }

  public ArgumentMatcher<ItemState> activeItem() {
    return isItem("activeItem", ItemState::isActive);
  }

  public ArgumentMatcher<ItemState> activeItem(String id) {
    return isItem("activeItem(%s)".formatted(id), itemState -> itemState.id().equals(IdUtils.toId(id)) && itemState.isActive());
  }

  public ArgumentMatcher<ItemState> inactiveItem() {
    return isItem("inactiveItem", item -> !item.isActive());
  }
  public ArgumentMatcher<ItemState> inactiveItem(String id) {
    return isItem("inactiveItem(%s)".formatted(id), item -> item.id().equals(IdUtils.toId(id)) && !item.isActive());
  }

  public ArgumentMatcher<ErrorState> inactiveError() {
    return isError("inactiveError", errorState -> !errorState.isActive());
  }

  public ArgumentMatcher<ErrorState> activeError() {
    return isError("activeError", ErrorState::isActive);
  }

  public ArgumentMatcher<ItemState> answeredItem(String itemId) {
    return isItem("answeredItem", itemState -> itemState.isAnswered() && (itemId == null || IdUtils.toId(itemId).equals(itemState.id())));
  }

  public ArgumentMatcher<ItemState> unansweredItem(String itemId) {
    return isItem("answeredItem", itemState -> {
      return !itemState.isAnswered() && (itemId == null || IdUtils.toId(itemId).equals(itemState.id()));
    });
  }

  public ArgumentMatcher<ItemState> isItem(String describe, Function<ItemState,Boolean> matches) {
    return new HamcrestArgumentMatcher(new BaseMatcher<ItemState>() {
      @Override
      public void describeTo(Description description) {
        description.appendText(describe);
      }

      @Override
      public boolean matches(Object item) {
        if (item instanceof ItemState itemState) {
          return matches.apply(itemState);
        }
        return false;
      }
    });
  }

  public ArgumentMatcher<ErrorState> isError(String describe, Function<ErrorState,Boolean> matches) {
    return new HamcrestArgumentMatcher(new BaseMatcher<ErrorState>() {
      @Override
      public void describeTo(Description description) {
        description.appendText(describe);
      }

      @Override
      public boolean matches(Object error) {
        if (error instanceof ErrorState errorState) {
          return matches.apply(errorState);
        }
        return false;
      }
    });
  }

  protected void assertValueEquals(final DialobSession session, final ItemId itemId, final Object expectedValue) {
    final ItemState itemState = session.getItemState(itemId).get();
    Assertions.assertEquals(expectedValue, itemState.getValue());
  }

  protected void assertNotRequired(final DialobSession session, final ItemId itemId) {
    final ItemState itemState = session.getItemState(itemId).get();
    Assertions.assertFalse(itemState.isRequired());
  }

  protected void assertRequired(final DialobSession session, final ItemId itemId) {
    final ItemState itemState = session.getItemState(itemId).get();
    Assertions.assertTrue(itemState.isRequired());
  }

  protected void assertReadOnly(final DialobSession session, final ItemId itemId) {
    final ItemState itemState = session.getItemState(itemId).get();
    Assertions.assertTrue(itemState.isReadOnly());
  }

  protected void assertNotReadOnly(final DialobSession session, final ItemId itemId) {
    final ItemState itemState = session.getItemState(itemId).get();
    Assertions.assertFalse(itemState.isReadOnly());
  }

  protected void assertVariableEquals(final DialobSession session, Object expected, final ItemId itemId) {
    final ItemState itemState = session.getItemState(itemId).get();
    Assertions.assertEquals(expected, itemState.getValue());
  }

  protected void assertErrorInactive(final DialobSession session, final ItemId itemId, final String errorCode) {
    final Collection<ErrorState> errorStates = session.errorStates().values();
    for (final ErrorState errorState : errorStates) {
      if (errorState.itemId().equals(itemId) && errorState.code().equals(errorCode) && errorState.isActive()) {
        Assertions.fail("Error " + itemId + "." + errorCode + " active");
      }
    }
  }

  protected void assertErrorDisabled(final DialobSession session, final ItemId itemId, final String errorCode) {
    final Collection<ErrorState> errorStates = session.errorStates().values();
    for (final ErrorState errorState : errorStates) {
      if (errorState.itemId().equals(itemId) && errorState.code().equals(errorCode) && !errorState.isDisabled()) {
        Assertions.fail("Error " + itemId + "." + errorCode + " disabled");
      }
    }
  }

  protected void assertErrorEnabled(final DialobSession session, final ItemId itemId, final String errorCode) {
    final Collection<ErrorState> errorStates = session.errorStates().values();
    for (final ErrorState errorState : errorStates) {
      if (errorState.itemId().equals(itemId) && errorState.code().equals(errorCode) && errorState.isDisabled()) {
        Assertions.fail("Error " + itemId + "." + errorCode + " disabled");
      }
    }
  }

  protected void assertErrorActive(final DialobSession session, final ItemId itemId, final String errorCode) {
    final Collection<ErrorState> errorStates = session.errorStates().values();
    for (final ErrorState errorState : errorStates) {
      if (errorState.itemId().equals(itemId) && errorState.code().equals(errorCode) && errorState.isActive()) {
        return;
      }
    }
    Assertions.fail("Error " + itemId + "." + errorCode + " not active");
  }

  protected void assertAllowedAction(final DialobSession session, Action.Type action) {
    final Optional<ItemState> questionnaireState = session.getItemState(toRef("questionnaire"));
    Assertions.assertTrue(questionnaireState.get().allowedActions().contains(action));
  }

  protected void assertDisallowedAction(final DialobSession session, Action.Type action) {
    final Optional<ItemState> questionnaireState = session.getItemState(toRef("questionnaire"));
    Assertions.assertFalse(questionnaireState.get().allowedActions().contains(action));
  }


  protected void assertErrorLabel(final DialobSession session, final ItemId itemId, final String errorCode, String label) {
    final Collection<ErrorState> errorStates = session.errorStates().values();
    for (final ErrorState errorState : errorStates) {
      if (errorState.itemId().equals(itemId) && errorState.code().equals(errorCode)) {
        Assertions.assertEquals(label, errorState.label());
        return;
      }
    }
    Assertions.fail("Error " + itemId + "." + errorCode + " not found");

  }

  protected ItemId toRef(String id) {
    return IdUtils.toId(id);
  }

}
