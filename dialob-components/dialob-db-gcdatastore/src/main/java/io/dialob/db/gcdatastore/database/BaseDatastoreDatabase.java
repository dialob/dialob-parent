/*
 * Copyright © 2015 - 2021 ReSys (info@dialob.io)
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
package io.dialob.db.gcdatastore.database;

import com.google.cloud.datastore.DatastoreException;
import com.google.rpc.Code;
import io.dialob.db.gcdatastore.repository.DatastoreRepository;
import io.dialob.db.spi.exceptions.DatabaseServiceDownException;
import io.dialob.db.spi.exceptions.DatabaseUnauthorizedException;
import io.dialob.db.spi.exceptions.DocumentConflictException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.util.ConcurrentModificationException;
import java.util.function.Function;

public class BaseDatastoreDatabase<T, R extends DatastoreRepository<T, String>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatastoreDatabase.class);

  protected final R repository;

  public BaseDatastoreDatabase(final R repository) {
    this.repository = repository;
  }

  protected <A> A handleAction(Function<R, A> action) {
    try {
      return action.apply(repository);
    } 
    catch (DatastoreException e) {
      LOGGER.warn("Datastore operation exception:", e);
      	switch (e.getCode()) {
      	case Code.ALREADY_EXISTS_VALUE:
        throw new DocumentConflictException(e.getMessage());
      	case Code.UNAUTHENTICATED_VALUE:
      	case Code.PERMISSION_DENIED_VALUE:
      		throw new DatabaseUnauthorizedException("Could not access: " + e.getMessage());
      	default:
      		throw new DatabaseServiceDownException("Database access failure: " + e.getMessage());
      	}
    }
    catch (ConcurrentModificationException e) {
      LOGGER.info("Datastore concurrent update:", e.getMessage());
      throw new DocumentConflictException(e.getMessage());
    }
  }


  @NonNull
  public T findOne(String tenantId, @NonNull String id, String rev) {
    return handleAction(repository -> {
      T document = repository.findOne(id);
      if (document == null) {
        throw new DocumentNotFoundException("not_found");
      }
      return document;
    });
  }

  @NonNull
  public T findOne(String tenantId, @NonNull String id) {
    return findOne(tenantId, id, null);
  }

  public boolean exists(String tenantId, @NonNull String id) {
    return handleAction(repository -> repository.exists(id));
  }

  public boolean delete(String tenantId, @NonNull String id) {
    return handleAction(repository -> {
      repository.delete(id);
      return true;
    });
  }

  @NonNull
  public T save(String tenantId, @NonNull T document) {
    return handleAction(repository -> repository.save(document));
  }

}
