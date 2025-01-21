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

import io.dialob.api.proto.Action;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.model.IdUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static io.dialob.session.engine.session.command.CommandFactory.*;
import static java.util.Objects.requireNonNull;

@Slf4j
public class ActionToCommandMapper implements Function<Action, Command<?>> {

  public static final ActionToCommandMapper INSTANCE = new ActionToCommandMapper();

  public static List<Command<?>> toCommands(Iterable<Action> actions) {
    return StreamSupport.stream(actions.spliterator(), false)
      .map(ActionToCommandMapper.INSTANCE)
      .filter(Objects::nonNull)
      .toList();
  }

  @Override
  public Command<?> apply(Action action) {
    var itemId = IdUtils.toIdNullable(action.getId());
    var ret = switch (action.getType()) {
      case ANSWER -> setAnswer(requireNonNull(itemId), action.getAnswer());
      case SET_VALUE -> setVariableValue(requireNonNull(itemId), action.getValue());
      case SET_FAILED -> setVariableFailed(requireNonNull(itemId));
      case NEXT -> nextPage();
      case PREVIOUS -> prevPage();
      case GOTO -> gotoPage(requireNonNull(itemId));
      case COMPLETE -> complete();
      case ADD_ROW -> addRow(requireNonNull(itemId));
      case DELETE_ROW -> deleteRow(requireNonNull(itemId));
      case SET_LOCALE -> action.getValue() instanceof String ? setLocale((String) action.getValue()) : null;
      case REMOVE_ANSWERS, RESET, ITEM, REMOVE_ITEMS, ERROR, REMOVE_ERROR, VALUE_SET, REMOVE_VALUE_SETS, NOT_FOUND,
           SERVER_ERROR, ROWS, LOCALE -> null;
    };
    if (ret == null) {
      LOGGER.debug("Action \"{}\" ignored.", action);
    }
    return ret;
  }

}
