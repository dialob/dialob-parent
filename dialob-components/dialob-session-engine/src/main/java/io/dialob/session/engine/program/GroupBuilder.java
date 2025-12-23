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

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.form.FormValidationError;
import io.dialob.api.proto.Action;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.session.engine.program.expr.NotOnPageExpression;
import io.dialob.session.engine.program.expr.arith.*;
import io.dialob.session.engine.program.model.Expression;
import io.dialob.session.engine.program.model.Group;
import io.dialob.session.engine.session.model.IdUtils;
import io.dialob.session.engine.session.model.ItemId;
import io.dialob.session.engine.session.model.ItemIdPartial;
import io.dialob.session.engine.session.model.ItemRef;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;

public class GroupBuilder extends AbstractItemBuilder<GroupBuilder,ProgramBuilder> implements BuilderParent {

  public static final Expression EMPTY_ARRAY_EXPRESSION = Constant.builder().valueType(ValueType.arrayOf(ValueType.STRING)).value(Collections.emptyList()).build();
  private List<ItemId> itemIds;

  private Expression canAddRowWhen = BooleanOperators.TRUE;

  private Expression canRemoveRowWhen = BooleanOperators.TRUE;

  enum Type {
    ROOT("questionnaire", true),
    PAGE("group", true),
    GROUP("group", true),
    ROWGROUP("rowgroup", false),
    SURVEYGROUP("surveygroup", true);

    @Getter
    private final String itemType;

    private final boolean haveSubItems;

    public boolean haveSubItems() {
      return haveSubItems;
    }

    Type(String itemType, boolean haveSubItems) {
      this.itemType = itemType;
      this.haveSubItems = haveSubItems;
    }
  }

  private final List<String> items = new ArrayList<>();

  @Getter
  private Type type;

  private String view;

  private String valueSetId;

  public GroupBuilder(ProgramBuilder programBuilder, GroupBuilder hoistingGroupBuilder, String id) {
    super(programBuilder, programBuilder, hoistingGroupBuilder, id);
  }

  public GroupBuilder root() {
    this.type = Type.ROOT;
    return this;
  }

  public GroupBuilder page() {
    this.type = Type.PAGE;
    return this;
  }

  public GroupBuilder group() {
    this.type = Type.GROUP;
    return this;
  }

  public GroupBuilder rowgroup() {
    this.type = Type.ROWGROUP;
    return this;
  }

  public GroupBuilder surveyGroup() {
    this.type = Type.SURVEYGROUP;
    return this;
  }

  public GroupBuilder setView(String view) {
    this.view = view;
    return this;
  }

  public GroupBuilder addItem(String id) {
    items.add(id);
    return this;
  }
  public GroupBuilder addItems(Collection<String> id) {
    items.addAll(id);
    return this;
  }

  public GroupBuilder setValueSet(String valueSetId) {
    this.valueSetId = valueSetId;
    return this;
  }

  public GroupBuilder setCanAddRowWhen(Expression canAddRowWhen) {
    this.canAddRowWhen = canAddRowWhen;
    return this;

  }

  public GroupBuilder setCanAddRowWhen(String canAddRowWhen) {
    if (StringUtils.isNotBlank(canAddRowWhen)) {
      compileExpression(canAddRowWhen, this::setCanAddRowWhen, FormValidationError.Type.CANADDROW, getIndex().orElse(null));
    }
    return this;
  }

  public GroupBuilder setCanRemoveRowWhen(Expression canRemoveRowWhen) {
    this.canRemoveRowWhen = canRemoveRowWhen;
    return this;
  }


  public GroupBuilder setCanRemoveRowWhen(String canRemoveRowWhen) {
    if (StringUtils.isNotBlank(canRemoveRowWhen)) {
      compileExpression(canRemoveRowWhen, this::setCanRemoveRowWhen, FormValidationError.Type.CANREMOVEROW, getIndex().orElse(null));
    }
    return this;
  }

  @NonNull
  public Collection<ItemId> getItemIds() {
    return Collections.unmodifiableList(itemIds);
  }

  protected boolean hoistsItem(String itemId) {
    return items.contains(itemId);
  }

  @Override
  public Optional<ValueType> getValueType() {
    if (type == Type.ROWGROUP) {
      return Optional.of(ValueType.arrayOf(ValueType.INTEGER));
    }
    return Optional.empty();
  }

  @Override
  protected void beforeExpressionCompilation(Consumer<FormValidationError> errorConsumer) {
    super.beforeExpressionCompilation(errorConsumer);
    itemIds = this.items.stream().map(item -> getProgramBuilder().findItemBuilder(item))
      .flatMap(Optional::stream)
      .map(AbstractItemBuilder::getId)
      .map(
        getType() == Type.ROWGROUP ?
        itemId -> itemId.withParent(new ItemIdPartial(getId())) :
        itemId -> itemId
      )
      .toList();
  }

  @Override
  protected void afterExpressionCompilation(Consumer<FormValidationError> errorConsumer) {
    super.afterExpressionCompilation(errorConsumer);
    requireBooleanExpression(canRemoveRowWhen, FormValidationError.Type.VISIBILITY, errorConsumer);
    requireBooleanExpression(canAddRowWhen, FormValidationError.Type.VISIBILITY, errorConsumer);

    Objects.requireNonNull(type, "group type missing");
    var id = getId();

    getHoistingGroup().ifPresent(hoistingGroupBuilder -> {
      if (activeWhen == BooleanOperators.TRUE) {
        activeWhen = new IsActiveOperator.Builder().itemId(hoistingGroupBuilder.getId()).build();
      } else {
        activeWhen = Operators.and(new IsActiveOperator.Builder().itemId(hoistingGroupBuilder.getId()).build(), activeWhen);
      }
    });

    Group.Builder builder = new Group.Builder()
      .id(id)
      .type(type.getItemType())
      .view(view)
      .itemsExpression(EMPTY_ARRAY_EXPRESSION)
      .isPrototype(false)
      .activeExpression(activeWhen)
      .canAddRowWhenExpression(canAddRowWhen)
      .canRemoveRowWhenExpression(canRemoveRowWhen)
      .className(Constant.builder().valueType(ValueType.arrayOf(ValueType.STRING)).value(classNames).build())
      .labelExpression(createLabelOperator(label))
      .descriptionExpression(createLabelOperator(description))
      .props(props);

    builder = switch (type) {
      case ROOT -> builder.allowedActionsExpression(new ConditionalListOperator.Builder()
          .addItems(Pair.of(Operators.not(IsOnFirstPage.instance()), Action.Type.PREVIOUS))
          .addItems(Pair.of(
            Operators.and(Operators.not(IsOnLastPage.instance()),
              Operators.not(new IsInvalidAnswersOnActivePage.Builder().pageContainerId(IdUtils.QUESTIONNAIRE_ID).build()))
            , Action.Type.NEXT))
          .addItems(Pair.of(Operators.not(IsAnyInvalidAnswersOperator.instance()), Action.Type.COMPLETE))
          .addItems(Pair.of(BooleanOperators.TRUE, Action.Type.ANSWER)
        ).build());
      // Disable page when it's not active
      case PAGE -> builder.disabledExpression(new NotOnPageExpression.Builder().page(id).build());
      case ROWGROUP -> builder.allowedActionsExpression(new ConditionalListOperator.Builder()
          .addItems(Pair.of(CanAddRowsOperator.of(id), Action.Type.ADD_ROW)
          ).build())
          .disabledExpression(getHoistingGroup().map(hoistingGroup -> Operators.isDisabled(hoistingGroup.getId())).orElse(null));
      // TODO hoisting page??
      // Disable group when parent group is not active
      case GROUP, SURVEYGROUP -> builder.disabledExpression(getHoistingGroup().map(hoistingGroup -> Operators.isDisabled(hoistingGroup.getId())).orElse(null));
    };

    if (type.haveSubItems()) {
        builder = builder
          .itemsExpression(Constant.builder().valueType(ValueType.arrayOf(ValueType.STRING)).value(itemIds).build());
     }

    builder = (switch (type) {
      // nothing here
      case GROUP, PAGE -> builder;
      case SURVEYGROUP -> builder
          .valueSetId(this.valueSetId);
      case ROWGROUP -> builder
          .valueType(ValueType.arrayOf(ValueType.INTEGER));
      case ROOT -> builder
          .availableItemsExpression(new ConditionalListOperator.Builder<ItemId>().addAllItems(itemIds.stream().map(item -> Pair.of((Expression) IsActiveOperator.of(item), item)).toList()).build())
          .isInvalidAnswersExpression(IsAnyInvalidAnswersOperator.instance());
    });

    getProgramBuilder().addItem(builder.build());
    if (type == Type.ROWGROUP) {
      // Row prototype for row group ..
      final ItemIdPartial rowGroupPrototypeId = new ItemIdPartial(id);
      getProgramBuilder().addItem(builder
        .id(rowGroupPrototypeId)
        .type("row")
        .isPrototype(true)
        .valueType(null)
        .itemsExpression(new RowItemsExpression.Builder().itemIds(this.itemIds.stream().map(itemId -> {
          String id1 = itemId.getValue();
          return new ItemRef(id1, rowGroupPrototypeId);
        }).toList()).build())
        .allowedActionsExpression(new ConditionalListOperator.Builder()
            .addItems(Pair.of(CanRemoveRowOperator.of(rowGroupPrototypeId), Action.Type.DELETE_ROW)
          ).build())
        .build()
      );
    }
  }
}
