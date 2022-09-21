package io.dialob.client.spi.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/*-
 * #%L
 * stencil-persistence
 * %%
 * Copyright (C) 2021 Copyright 2021 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.dialob.client.api.DialobClient;
import io.dialob.client.api.DialobDocument.FormReleaseDocument;
import io.dialob.client.spi.DialobComposerImpl;
import io.dialob.client.spi.migration.MigrationSupport;
import io.dialob.client.spi.support.RepositoryToStaticData;
import io.dialob.client.spi.test.config.DialobClientImplForTests;
import io.dialob.client.spi.test.config.PgProfile;
import io.dialob.client.spi.test.config.PgTestTemplate;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(PgProfile.class)
public class ImportReleasePgTest extends PgTestTemplate {

  @Test
  public void importIntoPGSQLTest() {
    final var store = repo().repoName("import-release").create().await().atMost(Duration.ofMinutes(1));
    final var client = DialobClientImplForTests.builder().store(store).build();
    final var composer = new DialobComposerImpl(client);
    

    final var src = getRelease(client);
    final var importState = composer.importRelease(src)
      .onFailure().invoke(e -> e.printStackTrace()).onFailure().recoverWithNull()
      .await().atMost(Duration.ofMinutes(1));

    
//    importState.getForms().values().stream().forEach(e -> {
//      
//      e.get
//    });
    
    // delete state
    final var expected = RepositoryToStaticData.toString(getClass(), "import_state.txt");
    final var actual = super.toRepoExport(store.getRepoName());
    Assertions.assertEquals(expected, actual);
    
  }
  
  public FormReleaseDocument getRelease(DialobClient client) {
    try {
      final var input = new FileInputStream(new File("src/test/resources/migration_dump.txt"));   
      return new MigrationSupport(client.getConfig().getMapper()).read(input);
    } catch(IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

}
