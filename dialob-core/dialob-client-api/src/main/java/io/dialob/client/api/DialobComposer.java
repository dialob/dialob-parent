package io.dialob.client.api;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.dialob.api.form.Form;
import io.dialob.api.form.FormPutResponse;
import io.dialob.client.api.DialobComposerDocument.FormDocument;
import io.dialob.client.api.DialobComposerDocument.FormRevision;
import io.dialob.client.api.DialobStore.BodyType;
import io.smallrye.mutiny.Uni;

public interface DialobComposer {

  Uni<ComposerState> get();
  Uni<ComposerDocumentState> get(@Nonnull String idOrNameOrIdAndRev);
  Uni<ComposerDocumentState> create(@Nonnull FormDocument asset);
  Uni<ComposerDocumentState> update(@Nonnull FormDocument asset);
  Uni<ComposerDocumentState> update(@Nonnull UpdateFormRevisionEntry asset);
  Uni<ComposerDocumentState> copyAs(@Nonnull String id, String copyToName);

  // delete anything that has id and version match
  Uni<ComposerState> delete(@Nonnull String id, @Nonnull String version);

  Uni<StoreDump> getStoreDump();
  Uni<FormPutResponse> validate(@Nonnull FormDocument asset);
  Uni<FormDocument> apply(@Nonnull FormCommands asset);


  @Value.Immutable @JsonSerialize(as = ImmutableComposerDocumentState.class) @JsonDeserialize(as = ImmutableComposerDocumentState.class)
  interface UpdateFormRevisionEntry extends Serializable {
    String getId();
    String getVersion();
    String getRevisionName();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableComposerDocumentState.class) @JsonDeserialize(as = ImmutableComposerDocumentState.class)
  interface ComposerDocumentState extends Serializable {
    FormDocument getForm();
    FormRevision getRevision();
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableComposerState.class) @JsonDeserialize(as = ImmutableComposerState.class)
  interface ComposerState extends Serializable {
    Map<String, FormDocument> getForms();
    Map<String, FormRevision> getRevs();
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
    Form getBody();
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
