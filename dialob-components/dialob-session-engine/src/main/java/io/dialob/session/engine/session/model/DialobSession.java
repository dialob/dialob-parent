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
package io.dialob.session.engine.session.model;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.dialob.common.Constants;
import io.dialob.session.engine.program.EvalContext;
import io.dialob.session.engine.session.command.Command;
import io.dialob.session.engine.session.protobuf.StateReader;
import io.dialob.session.engine.session.protobuf.StateWriter;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@EqualsAndHashCode
@ToString
@Slf4j
@AllArgsConstructor
//@Value.Builder
//@Value.Style(
//  isSetOnBuilder = true,
//  jakarta = true,
//  jdkOnly = true,
//  overshadowImplementation = true,
//  visibility = Value.Style.ImplementationVisibility.PACKAGE
//)
public class DialobSession implements EvalContext.SessionFacade, Serializable {

//  public static final class Builder extends DialobSessionBuilder {}

  @Serial
  private static final long serialVersionUID = 1180110179877247767L;

  public static final ItemRef QUESTIONNAIRE_REF = (ItemRef) IdUtils.toId(Constants.QUESTIONNAIRE);

  @Getter
  @NonNull
  private final String tenantId;

  @Getter
  private final String id;

  @Getter
  private String revision;

//  @NonNull
  private Instant lastUpdate;

  private Instant completed;

  private Instant opened;

  @Getter
  @Setter
  private String language;

  @Getter
  @NonNull
  private ItemStates itemStates;

  // TODO move this to DialobProgram
  @NonNull
  private final ItemStates prototypes;

  public void writeTo(StateWriter output) throws IOException {
    output.writeString(tenantId);
    output.writeNullableString(id);
    output.writeNullableString(revision);
    output.writeString(language);
    output.writeDate(lastUpdate);
    output.writeNullableDate(completed);
    output.writeNullableDate(opened);
    itemStates.writeTo(output);
    prototypes.writeTo(output);
  }

  public static DialobSession readFrom(StateReader input) throws IOException {
    var tenantId = input.readString();
    var id = input.readNullableString();
    var revision = input.readNullableString();
    var language = input.readString();
    var lastUpdate = input.readDate();
    var completed = input.readNullableDate();
    var opened = input.readNullableDate();
    var itemStates = ItemStates.readFrom(input);
    var prototypes = ItemStates.readFrom(input);
    return new DialobSession(
      tenantId,
      id,
      revision,
      lastUpdate,
      completed,
      opened,
      language,
      itemStates,
      prototypes
    );
  }

  public DialobSession withId(@NonNull String id) {
    if (Objects.equals(this.id, id)) {
      return this;
    }
    return new DialobSession(
      this.tenantId,
      id,
      this.revision,
      this.lastUpdate,
      this.completed,
      this.opened,
      this.language,
      this.itemStates,
      this.prototypes
    );
  }

  public DialobSession updateItemStatesTo(@NonNull ItemStates itemStates) {
    lastUpdate = Instant.now();
    if (opened == null) {
      opened = lastUpdate;
    }
    revision = Integer.toString(ThreadLocalRandom.current().nextInt());
    LOGGER.trace("{} updated to rev {}", getId(), revision);
    this.itemStates = itemStates;
    return this;
  }


  @NonNull
  public ItemState getRootItem() {
    return getItemState(QUESTIONNAIRE_REF)
      .orElseThrow(() -> new IllegalStateException("Could not find questionnaire from " + getId()));
  }

  public Optional<ItemState> getItemState(@NonNull ItemId id) {
    return Optional.ofNullable(itemStates().get(id));
  }

  public void accept(@NonNull DialobSessionVisitor visitor) {
    visitor.start();

    // --
    visitor.visitItemStates().ifPresent(itemVisitor -> {
      itemStates().values().forEach(itemVisitor::visitItemState);
      itemVisitor.end();
    });

    // --
    visitor.visitValueSetStates().ifPresent(valueSetVisitor -> {
      valueSetStates().values().forEach(valueSetVisitor::visitValueSetState);
      valueSetVisitor.end();

    });

    // --
    visitor.visitErrorStates().ifPresent(errorVisitor -> {
      errorStates().values().forEach(errorVisitor::visitErrorState);
      errorVisitor.end();
    });

    visitor.end();
  }

  /**
   *
   * @param evalContext execution context
   * @param command object to execute within context
   */
  public DialobSession applyCommand(@NonNull EvalContext evalContext, @NonNull Command command) {
    return new GenericCommandExecutor(this).applyCommand(evalContext, command);
  }

  @NonNull
  public Instant getLastUpdate() {
    return lastUpdate;
  }

  @Nullable
  public Instant getCompleted() {
    return completed;
  }

  @Nullable
  public Instant getOpened() {
    return opened;
  }


  @Nullable
  public Instant getLastAnswer() {
    return lastUpdate;
  }

  public boolean isCompleted() {
    return completed != null;
  }

  public boolean complete() {
    if (this.completed == null) {
      this.completed = Instant.now();
    }
    return isCompleted();
  }

  @NonNull
  public Map<ItemId, ItemState> itemStates() {
    return itemStates.itemStates();
  }

  @Override
  public ItemStates prototypes() {
    return prototypes;
  }

  @NonNull
  public Map<ValueSetId, ValueSetState> valueSetStates() {
    return itemStates.valueSetStates();
  }

  @NonNull
  public Map<ErrorId, ErrorState> errorStates() {
    return itemStates.errorStates();
  }

}
