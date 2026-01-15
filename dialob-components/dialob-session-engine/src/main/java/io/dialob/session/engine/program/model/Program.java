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


@Value.Builder
@Value.Style(jdkOnly = true, jdk9Collections = true, overshadowImplementation = true, visibility = Value.Style.ImplementationVisibility.PACKAGE)
public record Program(
  @NonNull String id,
  @NonNull Item rootItem,
  @NonNull List<Item> items,
  @NonNull List<ValueSet> valueSets
) implements ProgramNode {

  public static final class Builder extends ProgramBuilder { }


  public void accept(ProgramVisitor visitor) {
    final List<io.dialob.session.engine.program.model.Error> errors = new ArrayList<>();
    visitor.startProgram(this);

    visitor.visitItems().ifPresent(itemVisitor -> {
      // root item required!!
      itemVisitor.visitItem(this.rootItem());
      this.items().forEach(item -> {
        itemVisitor.visitItem(item);
        if (item instanceof DisplayItem displayItem) {
          errors.addAll(displayItem.errors());
        }
      });
      itemVisitor.end();
    });

    visitor.visitErrors().ifPresent(errorVisitor -> {
      errors.forEach(errorVisitor::visitError);
      errorVisitor.end();
    });

    visitor.visitValueSets().ifPresent(valueSetVisitor -> {
      this.valueSets().forEach(valueSetVisitor::visitValueSet);
      valueSetVisitor.end();
    });

    visitor.end();
  }

  public Stream<Item> findItemsBy(Predicate<ItemId> matcher) {
    Stream<Item> itemStream = this.items().stream().filter(item -> matcher.test(item.id()));
    if (matcher.test(this.rootItem().id())) {
      return Stream.concat(Stream.of(this.rootItem()), itemStream);
    }
    return itemStream;
  }

  public Optional<Item> getItem(ItemId id) {
    if (IdUtils.QUESTIONNAIRE_ID.equals(id)) {
      return Optional.of(this.rootItem());
    }
    for (Item item : this.items()) {
      if (id.equals(item.id())) {
        return Optional.of(item);
      }
    }
    return Optional.empty();
  }
}
