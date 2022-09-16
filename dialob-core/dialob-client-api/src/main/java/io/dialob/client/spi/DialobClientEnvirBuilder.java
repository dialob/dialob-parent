package io.dialob.client.spi;

import java.io.InputStream;

import io.dialob.client.api.DialobClient.EnvirBuilder;
import io.dialob.client.api.DialobClient.EnvirCommandFormatBuilder;
import io.dialob.client.api.DialobClient.ProgramEnvir;
import io.dialob.client.api.DialobStore.BodyType;
import io.dialob.client.api.DialobStore.StoreEntity;
import io.dialob.client.api.ImmutableBodySource;
import io.dialob.client.spi.support.DialobAssert;
import io.dialob.client.spi.support.Sha2;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DialobClientEnvirBuilder implements EnvirBuilder {
  private final DialobProgramEnvirFactory factory;
  private ProgramEnvir envir;
  
  @Override
  public EnvirCommandFormatBuilder addCommand() {
    final EnvirBuilder enviBuilder = this;
    return new EnvirCommandFormatBuilder() {
      private String id;
      private BodyType type;
      private String commandJson;
      private StoreEntity entity;
      private boolean cachless;
      
      @Override
      public EnvirCommandFormatBuilder id(String externalId) {
        this.id = externalId;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder tag(String commandJson) {
        this.type = BodyType.TAG;
        this.commandJson = commandJson;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder form(String commandJson) {
        this.type = BodyType.FORM;
        this.commandJson = commandJson;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder form(InputStream commandJson) {
        this.type = BodyType.FORM;
        this.commandJson = factory.getConfig().getMapper().toString(commandJson);
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder tag(StoreEntity entity) {
        this.type = BodyType.TAG;
        this.entity = entity;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder form(StoreEntity entity) {
        this.type = BodyType.FORM;
        this.entity = entity;
        return this;
      }
      @Override
      public EnvirCommandFormatBuilder cachless() {
        this.cachless = true;
        return this;
      }
      @Override
      public EnvirBuilder build() {
        DialobAssert.notNull(id, () -> "id must be defined!");
        DialobAssert.isTrue(commandJson != null || entity != null, () -> "commandJson or entity must be defined!");
        DialobAssert.isTrue(commandJson == null || entity == null, () -> "commandJson and entity can't be both defined!");

        factory.add(ImmutableBodySource.builder()
            .id(id)
            .bodyType(type)
            .hash(entity == null ? Sha2.blob(commandJson) : entity.getHash())
            .value(entity == null ? commandJson : entity.getBody())
            .build(), cachless);
        return enviBuilder;
      }
    };
  }
  @Override
  public EnvirBuilder from(ProgramEnvir envir) {
    this.envir = envir;
    return this;
  }
  @Override
  public ProgramEnvir build() {
    return factory.add(envir).build();
  }

}
