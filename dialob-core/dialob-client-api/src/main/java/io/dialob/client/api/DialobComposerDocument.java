package io.dialob.client.api;

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.dialob.api.form.Form;

// Entities that are body for StoreEntity.body
public interface DialobComposerDocument {
  
  @Value.Immutable @JsonSerialize(as = ImmutableFormRevision.class) @JsonDeserialize(as = ImmutableFormRevision.class)
  interface FormRevision extends DialobComposerDocument {
    String getId(); // unique id
    String getHead(); //latest form id
    @Nullable String getVersion(); // not really nullable, just in serialization
    
    LocalDateTime getCreated();
    LocalDateTime getUpdated();    
    List<FormRevisionEntry> getEntries();
  }
  
  
  @Value.Immutable @JsonSerialize(as = ImmutableFormRevisionEntry.class) @JsonDeserialize(as = ImmutableFormRevisionEntry.class)
  interface FormRevisionEntry extends DialobComposerDocument {
    String getId(); // unique id
    @Nullable String getVersion(); // not really nullable, just in serialization
    String getRevisionName();
    String getFormId();
    LocalDateTime getCreated();    
  }
  
  @Value.Immutable @JsonSerialize(as = ImmutableFormDocument.class) @JsonDeserialize(as = ImmutableFormDocument.class)
  interface FormDocument extends DialobComposerDocument {
    Form getValue();
  }
}