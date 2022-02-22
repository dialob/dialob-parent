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
package io.dialob.db.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dialob.db.spi.exceptions.DocumentCorruptedException;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import io.dialob.db.spi.spring.AbstractDocumentDatabase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractFileDatabase<F> extends AbstractDocumentDatabase<F> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileDatabase.class);

  private final Path path;

  private final ObjectMapper objectMapper;

  public AbstractFileDatabase(
    Class<F> documentClass,
    String directory,
    ObjectMapper objectMapper) {
    super(documentClass);
    File pathFile = new File(directory);
    if (pathFile.exists()) {
      if (pathFile.isFile()) {
        throw new RuntimeException(pathFile.getAbsolutePath() + " is file");
      }
    } else {
      pathFile.mkdirs();
    }
    this.path = pathFile.toPath();
    this.objectMapper = objectMapper;
  }

  protected File fileRef(String id) {
    return path.resolve(id + ".json").toFile();
  }

  @NonNull
  public F findOne(String tenantId, @NonNull String id, String rev) {
    File file = fileRef(id);
    if (!file.exists()) {
      throw new DocumentNotFoundException("document " + id + " do not exists");
    }
    return loadFile(file);
  }

  public F loadFile(File file) {
    try {
      return objectMapper.readValue(file, getDocumentClass());
    } catch (IOException e) {
      LOGGER.error("File " + file.getAbsoluteFile() + " is corrupted.", e);
    }
    return null;
  }

  @NonNull
  public F findOne(String tenantId, @NonNull String id) {
    return findOne(tenantId, id, null);
  }

  protected void forAllFiles(@NonNull final Consumer<File> fileConsumer) {
    List<String> files = new ArrayList<>();
    try(final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
      directoryStream.forEach(p -> fileConsumer.accept(p.toFile()));
    } catch (IOException e) {
      LOGGER.error("failed to read directory ", e);
    }
  }

  public String fileBaseName(File file) {
    String name = file.getName();
    if(name.endsWith(".json")) {
      return name.substring(0, name.length() - 5);
    }
    return name;
  }

  public boolean exists(String tenantId, @NonNull String id) {
    File file = fileRef(id);
    return file.exists();
  }

  public boolean delete(String tenantId, @NonNull String id) {
    return fileRef(id).delete();
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
        updateDocumentRev(document, Integer.toString(Integer.parseInt(rev) + 1));
      } catch(DocumentNotFoundException e) {
        initNewDocument(document);
      }
    } else {
      initNewDocument(document);
    }
    try {
      objectMapper.writeValue(fileRef(id(document)), document);
    } catch (IOException e) {
      LOGGER.error("Failed to write document " + id, e);
      throw new DocumentCorruptedException("Cannot update document " + id);
    }
    return document;
  }

  private void initNewDocument(F document) {
    updateDocumentId(document, createUuid());
    updateDocumentRev(document, "1");
  }


  protected String createUuid() {
    return UUID.randomUUID().toString().replace("-","");
  }
}
