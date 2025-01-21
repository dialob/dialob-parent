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
package io.dialob.db.mongo.database;

import com.mongodb.Function;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.spi.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * @param <T> mongo persistent type
 * @param <R> mongo repository type
 */
@Slf4j
public abstract class BaseMongoDbDatabase<T, M extends T, R extends MongoRepository<M, String>> {

  protected final R repository;

  BaseMongoDbDatabase(final R repository) {
    this.repository = repository;
  }

  <A> A doMongo(Function<R, A> action) {
    try {
      return action.apply(repository);
    } catch (PermissionDeniedDataAccessException e) {
      throw new DatabaseUnauthorizedException("Could not access: " + e.getMessage());
    } catch (ConcurrencyFailureException e) {
      throw new DocumentConflictException(e.getMessage());
    } catch (NonTransientDataAccessException e) {
      LOGGER.error("Database access failure.", e);
      throw new DatabaseServiceDownException("Database access failure: " + e.getMessage());
    }
  }

  protected String toMongoId(String id) {
    return id;
  }

  protected String toPublicId(String id) {
    return id;
  }

  protected T toPublic(T document) {
    return (T) document;
  }

  protected abstract M toMongo(T document);

  @NonNull
  public T findOne(@NonNull String tenantId, @NonNull String id, String rev) {
    return doMongo(repository -> {
      final String mongoId = toMongoId(id);
      Optional<M> document = Optional.empty();
      if (mongoId != null) {
        document = repository.findById(mongoId);
      }
      if (!document.isPresent()) {
        throw new DocumentNotFoundException("not_found");
      }
      return document.get();
    });
  }

  @NonNull
  public T findOne(@NonNull String tenantId, @NonNull String id) {
    return findOne(tenantId, id, null);
  }

  public boolean exists(@NonNull String tenantId, @NonNull String id) {
    return doMongo(repository -> {
      final String mongoId = toMongoId(id);
      if (mongoId == null) {
        return false;
      }
      return repository.existsById(mongoId);
    });
  }

  public boolean delete(String tenantId, @NonNull String id) {
    return doMongo(repository -> {
      final String mongoId = toMongoId(id);
      if (mongoId == null) {
        throw new DocumentNotFoundException("not_found");
      }
      repository.deleteById(mongoId);
      return true;
    });
  }

  @NonNull
  public T save(String tenantId, @NonNull T document) {
    return doMongo(repository -> {
      final M mongoDocument = toMongo(document);
      if (mongoDocument == null) {
        throw new DocumentCorruptedException("invalid_id");
      }
      return toPublic(repository.save(mongoDocument));
    });
  }

}
