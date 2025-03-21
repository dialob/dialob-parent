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

  private SessionSettings session = new SessionSettings();

  private DatabaseSettings db = new DatabaseSettings();

  private DatabaseSettings formDatabase = new DatabaseSettings();

  private DatabaseSettings questionnaireDatabase = new DatabaseSettings();

  private TenantSettings tenant = new TenantSettings();

  private ApiSettings api = new ApiSettings();

  private SecuritySettings security = new SecuritySettings();

  private Map<String,SubmitHandlerSettings> submitHandlers = new HashMap<>();

  private DialobAssetsSettings assets = new DialobAssetsSettings();

  private AwsSettings aws = new AwsSettings();

  private AzureSettings azure = new AzureSettings();

  private GcpSettings gcp = new GcpSettings();

  Map<String, Tags> tags = new HashMap<>();

  private FunctionSettings function = new FunctionSettings();

  @Data
  public static class DialobAssetsSettings {

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
    private DatabaseType databaseType;

    private JdbcSettings jdbc = new JdbcSettings();

    private FileSettings file = new FileSettings();

    private S3Settings s3 = new S3Settings();

    private AzureBlobStorageSettings azureBlobStorage = new AzureBlobStorageSettings();


    @Data
    public static class JdbcSettings {
      private String schema;

      private Map<String,String> remap;

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

    private SnsSettings sns = new SnsSettings();

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

    BlobStorage blobStorage = new BlobStorage();

    @Data
    public static class BlobStorage {
      Optional<String> endpoint =  Optional.empty();
    }

  }

  @Data
  public static class GcpSettings {
    boolean enabled = false;

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
