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
package io.dialob.api.proto;

import io.dialob.api.annotation.Nullable;

public final class ActionsFactory {

  public static final Action PREVIOUS_ACTION = new Action.Builder().type(Action.Type.PREVIOUS).build();
  public static final Action NEXT_ACTION = new Action.Builder().type(Action.Type.NEXT).build();
  public static final Action REMOVE_ANSWERS_ACTION = new Action.Builder().type(Action.Type.REMOVE_ANSWERS).build();

  private ActionsFactory() {}

  public static Action answer(String questionId, Object answer) {
    return new Action.Builder().id(questionId).answer(answer).type(Action.Type.ANSWER).build();
  }

  public static Action removeAnswers() {
    return REMOVE_ANSWERS_ACTION;
  }

  public static Action next() {
    return NEXT_ACTION;
  }

  public static Action previous() {
    return PREVIOUS_ACTION;
  }

  public static Action complete(String questionnaireId) {
    return new Action.Builder().type(Action.Type.COMPLETE).id(questionnaireId).build();
  }

  public static Action gotoPage(String pageId) {
    return new Action.Builder().type(Action.Type.GOTO).id(pageId).build();
  }

  public static Action addRow(String groupId) {
    return new Action.Builder().type(Action.Type.ADD_ROW).id(groupId).build();
  }

  public static Action deleteRow(String rowId) {
    return new Action.Builder().type(Action.Type.DELETE_ROW).id(rowId).build();
  }

  public static Action rows(String tableId, String ...ids) {
    return new Action.Builder().type(Action.Type.ROWS).id(tableId).addIds(ids).build();
  }

  public static Action rows(String tableId, Iterable<String> ids) {
    return new Action.Builder().type(Action.Type.ROWS).id(tableId).ids(ids).build();
  }

  public static Action setValue(String variableId, Object value) {
    return new Action.Builder().type(Action.Type.SET_VALUE).id(variableId).value(value).build();
  }

  public static Action setFailed(String variableId, Object error) {
    return new Action.Builder().type(Action.Type.SET_FAILED).id(variableId).value(error).build();
  }

  public static Action setLocale(String locale) {
    return new Action.Builder().type(Action.Type.SET_LOCALE).value(locale).build();
  }

  public static Actions actions(@Nullable String rev, Action ...actions) {
    return new Actions.Builder().rev(rev).addActions(actions).build();
  }

  public static Actions actions(Action ...actions) {
    return actions(null, actions);
  }

}
