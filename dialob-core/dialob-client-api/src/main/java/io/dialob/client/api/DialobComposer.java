package io.dialob.client.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormEntity;
import io.dialob.api.form.FormPutResponse;
import io.dialob.api.form.FormTag;
import io.dialob.api.rest.Response;
import io.dialob.client.api.DialobStore.BodyStatus;
import io.dialob.client.api.DialobStore.BodyType;
import io.dialob.client.api.DialobStore.HistoryEntity;
import io.smallrye.mutiny.Uni;

public interface DialobComposer {

  Uni<ComposerState> get();
  Uni<ComposerEntity<?>> get(@Nonnull String idOrNameOrIdAndRev);
  Uni<HistoryEntity> getHistory(@Nonnull String id, @Nonnull String rev);
  
  Uni<ComposerState> update(@Nonnull Update asset);
  Uni<ComposerState> create(@Nonnull Create asset);
  Uni<ComposerState> delete(@Nonnull String id, @Nonnull String rev);
  Uni<ComposerState> copyAs(@Nonnull CopyAs copyAs);
  
  Uni<StoreDump> getStoreDump();
  Uni<FormPutResponse> validate(@Nonnull Form asset);
  Uni<Form> apply(@Nonnull FormCommands asset);
  
  
//TODO::
  @Value.Immutable @JsonSerialize(as = ImmutableComposerState.class) @JsonDeserialize(as = ImmutableComposerState.class)
  interface ComposerState extends Serializable {
    Map<String, ComposerEntity<FormTag>> getTags();
    Map<String, ComposerEntity<FormAndTags>> getForms();
  }
//TODO::
  @Value.Immutable @JsonSerialize(as = ImmutableFormAndTags.class) @JsonDeserialize(as = ImmutableFormAndTags.class)
  interface FormAndTags extends Serializable {
    Form getForm();
    List<FormTag> getTags();
  }

  @Value.Immutable @JsonSerialize(as = ImmutableComposerEntity.class) @JsonDeserialize(as = ImmutableComposerEntity.class)
  interface ComposerEntity<A> {
    String getId();
    A getBody();
    List<Response> getErrors();
    BodyStatus getStatus();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableStoreDump.class) @JsonDeserialize(as = ImmutableStoreDump.class)
  interface StoreDump extends Serializable {
    String getId();
    List<StoreDumpValue> getValue();
  }

  @Value.Immutable @JsonSerialize(as = ImmutableStoreDumpValue.class) @JsonDeserialize(as = ImmutableStoreDumpValue.class)
  interface StoreDumpValue extends Serializable {
    String getId();
    String getHash();
    BodyType getBodyType();
    String getValue();
  }

  @Value.Immutable @JsonSerialize(as = ImmutableCreate.class) @JsonDeserialize(as = ImmutableCreate.class)
  interface Create extends Serializable {
    BodyType getType();
    FormEntity getBody();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableCopyAs.class) @JsonDeserialize(as = ImmutableCopyAs.class)
  interface CopyAs extends Serializable {
    String getId(); // id of the entity
    String getName(); // new name of the entity
  }

  @Value.Immutable @JsonSerialize(as = ImmutableUpdate.class) @JsonDeserialize(as = ImmutableUpdate.class)
  interface Update extends Serializable {
    String getId();
    String getRev();
    FormEntity getFormEntity();
  }
   
  
  @Value.Immutable @JsonSerialize(as = ImmutableFormCommands.class) @JsonDeserialize(as = ImmutableFormCommands.class)
  interface FormCommands {
    Form getForm();
    List<FormCommand> getValues();
  }
  
  interface FormCommand extends Serializable { }

  interface ItemCopyCommand extends FormCommand {
    String getItemId();
  }
  
  interface ItemRenameCommand extends FormCommand {
    String getOldId();
    String getNewId();
  }  
  

}
