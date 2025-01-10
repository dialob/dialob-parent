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
package io.dialob.cloud.azure;

import com.azure.storage.blob.BlobServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DialobCloudAzureAutoConfigurationTest {

  @Test
  public void testDialobCloudAzureAutoConfiguration() {
    new WebApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(
        DialobCloudAzureAutoConfiguration.class
      ))
      .withPropertyValues(
        "dialob.azure.enabled=true",
        "dialob.azure.blob-storage.endpoint=https://test.blob.core.windows"
      )
      .run(context -> {
        assertThat(context)
          .hasSingleBean(BlobServiceClient.class);
      });
  }

  @Test
  public void testDialobCloudAzureAutoConfigurationDisabled() {
    new WebApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(
        DialobCloudAzureAutoConfiguration.class
      ))
      .withPropertyValues(
        "dialob.azure.enabled=false"
      )
      .run(context -> {
        assertThat(context)
          .doesNotHaveBean(BlobServiceClient.class);
      });
  }

}
