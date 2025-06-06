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
package io.dialob.session.engine.program.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


@Value.Immutable
public interface Program extends ProgramNode {

  @NonNull
  String getId();

  @NonNull
  Item getRootItem();

  List<Item> getItems();

  List<ValueSet> getValueSets();

  default void accept(ProgramVisitor visitor) {
    final List<io.dialob.session.engine.program.model.Error> errors = new ArrayList<>();
    visitor.startProgram(this);

    visitor.visitItems().ifPresent(itemVisitor -> {
      // root item required!!
      itemVisitor.visitItem(getRootItem());
      getItems().forEach(item -> {
        itemVisitor.visitItem(item);
        if (item instanceof DisplayItem displayItem) {
          errors.addAll(displayItem.getErrors());
        }
      });
      itemVisitor.end();
    });

    visitor.visitErrors().ifPresent(errorVisitor -> {
      errors.forEach(errorVisitor::visitError);
      errorVisitor.end();
    });

    visitor.visitValueSets().ifPresent(valueSetVisitor -> {
      getValueSets().forEach(valueSetVisitor::visitValueSet);
      valueSetVisitor.end();
    });

    visitor.end();
  }

  default Stream<Item> findItemsBy(Predicate<ItemId> matcher) {
    Stream<Item> itemStream = getItems().stream().filter(item -> matcher.test(item.getId()));
    if (matcher.test(getRootItem().getId())) {
      return Stream.concat(Stream.of(getRootItem()), itemStream);
    }
    return itemStream;
  }

  default Optional<Item> getItem(ItemId id) {
    if (IdUtils.QUESTIONNAIRE_ID.equals(id)) {
      return Optional.of(getRootItem());
    }
    for (Item item : getItems()) {
      if (id.equals(item.getId())) {
        return Optional.of(item);
      }
    }
    return Optional.empty();
  }
}
