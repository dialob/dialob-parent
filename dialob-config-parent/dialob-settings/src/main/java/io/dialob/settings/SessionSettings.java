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
package io.dialob.settings;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SessionSettings {

  private SecuritySettings security = new SecuritySettings();

  private AutosaveSettings autosave;

  private CacheSettings cache = new CacheSettings();

  private EndpointSettings rest = new EndpointSettings();

  private boolean returnStackTrace;

  private SockJSSettings sockjs = new SockJSSettings();

  @Data
  public static class SecuritySettings {

    private boolean enabled;

  }

  @Data
  public static class EndpointSettings {

    private String context;

    private boolean enabled;

    private boolean requireAuthenticated = false;

    private Map<String,CorsSettings> cors = new HashMap<>();

  }

  @Data
  public static class AutosaveSettings {

    private boolean enabled;

    private long interval;

  }

  @Data
  public static class SockJSSettings {

    private String libraryUrl;

    @NotEmpty
    private List<String> allowedOrigins = new ArrayList<>();

    private boolean enabled = true;

    private boolean webSocketEnabled = true;

    private UrlAttributes urlAttributes = new UrlAttributes();

    @NotEmpty
    private String contextPath = "/socket/{tenantId}/{sessionId}";

    private int maxTextMessageBufferSize = 65536;

    private int maxBinaryMessageBufferSize = 65536;

    private int sendTimeLimit = 5000;

    @Data
    public static class UrlAttributes {

      private String sessionId = "sessionId";

      private String tenantId = "tenantId";

    }
  }
}
