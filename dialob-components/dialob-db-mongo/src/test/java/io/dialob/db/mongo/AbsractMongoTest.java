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
package io.dialob.db.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class AbsractMongoTest {

  protected static MongoDBContainer mongod = new MongoDBContainer(DockerImageName.parse("mongo"))
    .withExposedPorts(27017)
    .withEnv("MONGO_INITDB_ROOT_USERNAME", "mongo")
    .withEnv("MONGO_INITDB_ROOT_PASSWORD", "mongo")
    ;

  @BeforeAll
  public static void startMongo() throws Exception {
    mongod.start();
  }

  protected static MongoClient createMongoClient() {
    return MongoClients.create(mongod.getConnectionString());
  }

  @AfterAll
  public static void stopMongo() {
    if (mongod != null) {
      mongod.stop();
      mongod = null;
    }
  }

}
