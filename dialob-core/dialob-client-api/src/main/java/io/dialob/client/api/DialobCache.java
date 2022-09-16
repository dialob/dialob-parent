package io.dialob.client.api;

import java.io.Serializable;
import java.util.Optional;

import org.immutables.value.Value;

import io.dialob.api.form.FormEntity;
import io.dialob.client.api.DialobStore.BodySource;
import io.dialob.program.DialobProgram;

public interface DialobCache {

  DialobCache withName(String name);
  
  Optional<DialobProgram> getProgram(BodySource src);
  Optional<FormEntity> getAst(BodySource src);
  
  DialobProgram setProgram(DialobProgram program, BodySource src);
  FormEntity setAst(FormEntity ast, BodySource src);
  
  void flush(String id);
  
  @Value.Immutable
  interface CacheEntry extends Serializable {
    String getId();
    String getRev();
    BodySource getSource();
    FormEntity getAst();
    Optional<DialobProgram> getProgram();
  }

}
