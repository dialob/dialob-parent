package io.dialob.client.spi;

import java.io.InputStream;

import io.dialob.client.api.DialobClient.BodySourceBuilder;
import io.dialob.client.api.DialobClientConfig;
import io.dialob.client.api.DialobStore.BodySource;
import io.dialob.client.api.DialobStore.BodyType;
import io.dialob.client.api.ImmutableBodySource;
import io.dialob.client.spi.support.DialobAssert;
import io.dialob.client.spi.support.Sha2;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BodySourceBuilderImpl implements BodySourceBuilder {
  
  private final DialobClientConfig config;
  private String id;
  private String src;

  @Override
  public BodySourceBuilder id(String id) {
    this.id = id;
    return this;
  }
  @Override
  public BodySourceBuilder syntax(String src) {
    this.src = src;
    return this;
  }
  @Override
  public BodySourceBuilder syntax(InputStream syntax) {
    this.src = config.getMapper().toString(syntax);
    return this;
  }
  @Override
  public BodySource build(BodyType type) {
    DialobAssert.notNull(id, () -> "id must be defined!");
    DialobAssert.notNull(src, () -> "src must be defined!");
    DialobAssert.notNull(type, () -> "type must be defined!");
    
    return ImmutableBodySource.builder().id(id).value(src).hash(Sha2.blob(src)).build();
  }
}
