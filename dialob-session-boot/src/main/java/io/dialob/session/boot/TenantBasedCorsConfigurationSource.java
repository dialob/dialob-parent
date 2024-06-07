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
package io.dialob.session.boot;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.security.tenant.Tenant;
import io.dialob.settings.CorsSettings;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Optional;
import java.util.function.Function;

public class TenantBasedCorsConfigurationSource implements CorsConfigurationSource {

  private final Function<String,CorsSettings> corsSettingsProvider;

  private final TenantFromRequestResolver tenantFromRequestResolver;

  public TenantBasedCorsConfigurationSource(@NonNull Function<String,CorsSettings> corsSettingsProvider,
                                            @NonNull TenantFromRequestResolver tenantFromRequestResolver) {
    this.corsSettingsProvider = corsSettingsProvider;
    this.tenantFromRequestResolver = tenantFromRequestResolver;
  }

  @Nullable
  @Override
  public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
    return corsSettingsProvider
      .andThen(corsSettings -> Optional.ofNullable(corsSettings == null ? corsSettingsProvider.apply("default") : corsSettings))
      .apply(resolveTenantFromRequest(request))
      .flatMap(CorsSettings::toCorsConfiguration)
      .orElse(null);
  }

  private String resolveTenantFromRequest(HttpServletRequest request) {
    return this.tenantFromRequestResolver.resolveTenantFromRequest(request).map(Tenant::getId).orElse(null);
  }
}
