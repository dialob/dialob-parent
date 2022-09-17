package io.dialob.client.spi.support;

import io.dialob.client.api.DialobStore.QueryBuilder;
import io.dialob.client.api.DialobStore.StoreEntity;
import io.dialob.client.api.DialobStore.StoreState;
import io.dialob.client.spi.PgSqlConfig;
import io.smallrye.mutiny.Uni;

public class DocumentQueryBuilder extends PersistenceCommands implements QueryBuilder {

  public DocumentQueryBuilder(PgSqlConfig config) {
    super(config);
  }

  @Override
  public Uni<StoreState> get() {
    return super.get();
  }

  @Override
  public Uni<StoreEntity> get(String id) {
    var result = super.getEntityState(id);
    return result.onItem().transform(entityState -> entityState.getEntity());
  }
}
