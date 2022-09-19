package io.dialob.client.api;

import java.io.Serializable;
import java.util.Optional;

import org.immutables.value.Value;

import io.dialob.client.api.DialobStore.StoreEntity;
import io.dialob.program.DialobProgram;

public interface DialobCache {

  DialobCache withName(String name);
  
  Optional<DialobProgram> getProgram(StoreEntity src);
  Optional<DialobComposerDocument> getAst(StoreEntity src);
  
  DialobProgram setProgram(DialobProgram program, StoreEntity src);
  DialobComposerDocument setAst(DialobComposerDocument ast, StoreEntity src);
  
  void flush(String id);
  
  @Value.Immutable
  interface CacheEntry extends Serializable {
    String getId();
    String getRev();
    StoreEntity getSource();
    DialobComposerDocument getAst();
    Optional<DialobProgram> getProgram();
  }

}
