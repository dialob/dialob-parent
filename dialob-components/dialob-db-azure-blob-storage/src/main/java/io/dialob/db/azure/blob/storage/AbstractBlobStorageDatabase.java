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
package io.dialob.db.azure.blob.storage;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.db.spi.spring.AbstractDocumentDatabase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public abstract class AbstractBlobStorageDatabase<F> extends AbstractDocumentDatabase<F> {

  private final ObjectMapper objectMapper;
  private final BlobContainerClient blobContainerClient;
  private final String prefix;

  public AbstractBlobStorageDatabase(
    @NonNull BlobContainerClient blobContainerClient,
    @NonNull Class<F> documentClass,
    @NonNull ObjectMapper objectMapper,
    @NonNull String prefix)
  {
    super(documentClass);
    this.blobContainerClient = blobContainerClient;
    this.objectMapper = objectMapper;
    this.prefix = prefix;

  }

  protected abstract String tenantPrefix(String tenantId);

  /**
   * Construct S3 object name
   *
   * @param tenantId
   * @param id
   * @return object name in storage
   */
  protected String objectName(String tenantId, String id) {
    return tenantPrefix(tenantId) + "/" + id;
  }

  protected String extractObjectName(String key) {
    return key.substring(key.lastIndexOf("/") + 1);
  }



  @NonNull
  public F findOne(String tenantId, @NonNull String id, String rev) {
    try {
      String objectName = objectName(tenantId, id);
      InputStream blob = this.blobContainerClient
        .getBlobClient(objectName)
        .downloadContent()
        .toStream();
      return loadFile(objectName, blob);
    } catch (BlobStorageException e) {
      if (e.getErrorCode() == BlobErrorCode.BLOB_NOT_FOUND) {
        throw new DocumentNotFoundException("No form document \"" + id + "\"");
      }
      throw e;
    }
  }

  public F loadFile(String objectName, InputStream inputStream) {
    try {
      return objectMapper.readValue(inputStream, getDocumentClass());
    } catch (IOException e) {
      LOGGER.error("Object {} is corrupted.", objectName, e);
    }
    return null;
  }

  @NonNull
  public F findOne(String tenantId, @NonNull String id) {
      return findOne(tenantId, id, null);
  }

  protected void forAllObjects(String tenantId, @NonNull final Consumer<BlobItem> fileConsumer) {
    this.blobContainerClient
      .listBlobs(new ListBlobsOptions()
        .setPrefix(tenantPrefix(tenantId))
        .setMaxResultsPerPage(200), Duration.ofSeconds(20))
      .forEach(fileConsumer);
}

  public boolean exists(String tenantId, @NonNull String id) {
    return this.blobContainerClient
      .getBlobClient(objectName(tenantId, id))
      .exists();
  }

  public boolean delete(String tenantId, @NonNull String id) {
    if (exists(tenantId, id)) {
      this.blobContainerClient
        .getBlobClient(objectName(tenantId, id))
        .delete();
      return true;
    }
    return false;
  }

  @NonNull
  public F save(String tenantId, @NonNull F document) {
    String id = id(document);
    String rev = rev(document);
    if (!StringUtils.isBlank(id)) {
      F previousVersion;
      try {
        previousVersion = findOne(tenantId, id);
        if (rev == null || !rev.equals(rev(previousVersion))) {
          throw new VersionConflictException(id + " revision " + rev(previousVersion) + " do not match with " + rev);
        }
        document = updateDocumentRev(document, Integer.toString(Integer.parseInt(rev) + 1));
      } catch(DocumentNotFoundException e) {
        document = initNewDocument(document);
        id = id(document);
      }
    } else {
      document = initNewDocument(document);
      id = id(document);
    }
    try {
      this.blobContainerClient
          .getBlobClient(objectName(tenantId, id))
            .upload(BinaryData.fromBytes(objectMapper.writeValueAsBytes(document)), true);
    } catch (JsonProcessingException | BlobStorageException e) {
      LOGGER.error("Failed to write document {}", id, e);
      throw new DocumentCorruptedException("Cannot update document " + id);
    }
    return document;
  }

  private F initNewDocument(F document) {
    document = updateDocumentId(document, createUuid());
    document = updateDocumentRev(document, "1");
    return document;
  }

  protected String createUuid() {
    return UUID.randomUUID().toString().replace("-","");
  }
}
