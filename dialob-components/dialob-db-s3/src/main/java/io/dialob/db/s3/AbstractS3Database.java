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
package io.dialob.db.s3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.db.spi.spring.AbstractDocumentDatabase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public abstract class AbstractS3Database<F> extends AbstractDocumentDatabase<F> {

  private final S3Client s3Client;
  private final ObjectMapper objectMapper;
  @Getter
  private final String bucketName;
  @Getter
  private final String prefix;

  public AbstractS3Database(
    @NonNull S3Client s3Client,
    @NonNull Class<F> documentClass,
    @NonNull ObjectMapper objectMapper,
    @NonNull String bucketName,
    @NonNull String prefix)
  {
    super(documentClass);
    this.s3Client = s3Client;
    this.objectMapper = objectMapper;
    this.bucketName = bucketName;
    this.prefix = prefix;
  }

  protected abstract String tenantPrefix(String tenantId);

  /**
   * Construct S3 object name
   *
   * @param tenantId the ID of the tenant, used as part of the S3 path prefix
   * @param id       the unique identifier of the object
   * @return object name in storage
   */
  protected String objectName(String tenantId, String id) {
    return tenantPrefix(tenantId) + "/" + id;
  }

  protected String extractObjectName(String key) {
    return key.substring(key.lastIndexOf("/") + 1);
  }



  @NonNull
  public F findOne(@NonNull String tenantId, @NonNull String id, String ignoredRev) {
    try {
      String objectName = objectName(tenantId, id);
      ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(GetObjectRequest.builder().bucket(this.bucketName).key(objectName).build());
      return loadFile(objectName, s3Object);
    } catch (NoSuchKeyException e) {
      throw new DocumentNotFoundException("No form document \"" + id + "\"");
    } catch (S3Exception e) {
      LOGGER.error("err", e);
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
  public F findOne(@NonNull String tenantId, @NonNull String id) {
      return findOne(tenantId, id, null);
  }

  protected void forAllObjects(String tenantId, @NonNull final Consumer<S3Object> fileConsumer) {
    ListObjectsV2Response result;
    String continuationToken = null;
    do {
      result = s3Client.listObjectsV2(ListObjectsV2Request.builder()
        .bucket(this.bucketName)
        .prefix(tenantPrefix(tenantId))
        .continuationToken(continuationToken)
        .build()
      );
      result.contents().forEach(fileConsumer);
      continuationToken = result.nextContinuationToken();
    } while (StringUtils.isNotBlank(continuationToken));
  }

  public boolean exists(@NonNull String tenantId, @NonNull String id) {
    try {
      HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
        .bucket(this.bucketName)
        .key(objectName(tenantId, id))
        .build());
      return response.sdkHttpResponse().isSuccessful();
    } catch (NoSuchKeyException e) {
      return false;
    }
  }

  public boolean delete(String tenantId, @NonNull String id) {
    if (exists(tenantId, id)) {
      s3Client.deleteObject(DeleteObjectRequest.builder()
        .bucket(this.bucketName)
        .key(objectName(tenantId, id))
        .build());
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
      s3Client.putObject(PutObjectRequest.builder()
          .bucket(bucketName)
          .key(objectName(tenantId, id))
          .build(),
          RequestBody.fromBytes(objectMapper.writeValueAsBytes(document))
        );
    } catch (JsonProcessingException | SdkClientException e) {
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
