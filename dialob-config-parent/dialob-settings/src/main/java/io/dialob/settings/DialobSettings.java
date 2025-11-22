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
package io.dialob.settings;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@ConfigurationProperties("dialob")
@Validated
@Data
public class DialobSettings {

  public enum DatabaseType {
    NONE,
    FILEDB,
    JDBC,
    S3,
    AZURE_BLOB_STORAGE
  }

  @Valid
  private SessionSettings session = new SessionSettings();

  @Valid
  private DatabaseSettings db = new DatabaseSettings();

  @Valid
  private DatabaseSettings formDatabase = new DatabaseSettings();

  @Valid
  private DatabaseSettings questionnaireDatabase = new DatabaseSettings();

  @Valid
  private TenantSettings tenant = new TenantSettings();

  @Valid
  private ApiSettings api = new ApiSettings();

  @Valid
  private SecuritySettings security = new SecuritySettings();

  private Map<String,SubmitHandlerSettings> submitHandlers = new HashMap<>();

  @Valid
  private DialobAssetsSettings assets = new DialobAssetsSettings();

  @Valid
  private AwsSettings aws = new AwsSettings();

  @Valid
  private AzureSettings azure = new AzureSettings();

  @Valid
  private GcpSettings gcp = new GcpSettings();

  Map<String, Tags> tags = new HashMap<>();

  @Valid
  private FunctionSettings function = new FunctionSettings();

  @Data
  public static class DialobAssetsSettings {

    @Valid
    private DialobAssetsServiceSettings service = new DialobAssetsServiceSettings();

  }


  @Data
  public static class DialobAssetsServiceSettings {

    private String url;

    private String authorization;

  }

  @Data
  public static class DatabaseSettings {

    @NotNull
    @Valid
    private DatabaseType databaseType;

    @Valid
    private JdbcSettings jdbc = new JdbcSettings();

    @Valid
    private FileSettings file = new FileSettings();

    @Valid
    private S3Settings s3 = new S3Settings();

    @Valid
    private AzureBlobStorageSettings azureBlobStorage = new AzureBlobStorageSettings();


    @Data
    public static class JdbcSettings {
      private String schema;

      private Map<String,String> remap = Map.of();

    }

    @Data
    public static class FileSettings {
      private String directory;
    }

    @Data
    public static class S3Settings {

      private String bucket;

      private String prefix = "";

    }

    @Data
    public static class AzureBlobStorageSettings {

      private String containerName;

      private String prefix = "";

      private String suffix;

    }

  }

  @Data
  public static class TenantSettings {
    public enum Mode {
      FIXED,
      URL_PARAM
    }

    @Valid
    private Mode mode = Mode.FIXED;

    private String fixedId = "00000000-0000-0000-0000-000000000000";

    private String env = "test";

    private String urlParameter = "tenantId";

    private Map<String,Set<String>> groupToTenants = new HashMap<>();

    private Map<String,Tenant> tenants = new HashMap<>();

    public record Tenant(
      String name
    ) {}

  }

  @Data
  public static class ApiSettings {

    private String contextPath = "/api";

    private String apiKeySalt = "secret";

    private List<DialobSettings.ApiSettings.ApiKey> apiKeys = new ArrayList<>();

    private List<io.dialob.settings.DialobSettings.ApiSettings.SecurityScheme> schemes;

    @Data
    public static class SecurityScheme {

      private String type;

      private String name;

      private String keyName;

      private String passAs;

    }

    @Data
    public static class ApiKey {

      private String tenantId;

      private String clientId;

      private String hash;

      private Set<String> permissions = new HashSet<>();

    }

    @Valid
    CorsSettings cors = new CorsSettings();
  }

  @Data
  public static class SecuritySettings {

    public enum AuthenticationMethod {
      OAUTH2,
      AWSELB,
      NONE
    }

    private boolean enabled;

    @Valid
    private AuthenticationMethod authenticationMethod = AuthenticationMethod.OAUTH2;

    private Map<String,Set<String>> groupPermissions = new HashMap<>();

    private String groupsClaim;

    @Data
    public static class AuditSettings {

      private boolean enabled;

    }
  }

  @Data
  public static class AwsSettings {

    boolean enabled = false;

    private String region = "eu-central-1";

    @Valid
    private SnsSettings sns = new SnsSettings();

    @Valid
    ElbSettings elb = new ElbSettings();

    @Data
    public static class SnsSettings {

      boolean enabled = false;

      String formEventsTopicARN;

      String questionnaireEventsTopicARN;

    }

    @Data
    public static class ElbSettings {

      boolean authEnabled = false;

      Set<String> algorithms = Set.of("ES256");

      Optional<String> principalRequestHeader = Optional.of("X-Amzn-Oidc-Identity");

      Optional<String> credentialsRequestHeader = Optional.of("X-Amzn-Oidc-Data");
    }

  }

  @Data
  public static class AzureSettings {

    boolean enabled = false;

    @Valid
    BlobStorage blobStorage = new BlobStorage();

    @Data
    public static class BlobStorage {
      Optional<String> endpoint =  Optional.empty();
    }

  }

  @Data
  public static class GcpSettings {
    boolean enabled = false;

    @Valid
    private PubSubSettings pubsub = new PubSubSettings();

    @Data
    public static class PubSubSettings {
      boolean enabled = false;

      String formEventsTopic;

      String questionnaireEventsTopic;
    }

  }

  @Data
  public static class Tags {

    Set<String> predefined = new HashSet<>();

  }

}
