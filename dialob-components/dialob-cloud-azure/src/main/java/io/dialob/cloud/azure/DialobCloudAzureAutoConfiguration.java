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
package io.dialob.cloud.azure;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import io.dialob.settings.DialobSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "dialob.azure", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(DialobSettings.class)
@Slf4j
public class DialobCloudAzureAutoConfiguration {

  @Bean
  public BlobServiceClient blobServiceClient(DialobSettings settings) {
    var azure = settings.getAzure();
    var builder = new BlobServiceClientBuilder();
    azure.getBlobStorage().getEndpoint().ifPresent(builder::endpoint);
    var credential = new DefaultAzureCredentialBuilder().build();
    builder.credential(credential);
    return builder.buildClient();
  }

}
