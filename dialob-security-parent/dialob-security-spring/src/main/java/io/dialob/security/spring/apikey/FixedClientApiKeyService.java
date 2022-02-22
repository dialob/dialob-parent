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
package io.dialob.security.spring.apikey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.immutables.value.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.dialob.security.key.ApiKey;
import io.dialob.security.key.ImmutableApiKey;
import io.dialob.security.spring.tenant.ImmutableTenantGrantedAuthority;
import lombok.Data;

@Value.Enclosing
public class FixedClientApiKeyService implements ClientApiKeyService, ApiKeyAuthoritiesProvider {

  @Data
  protected static class ApiKeyEntry {

    String clientId;

    String hash;

    String tenantId;

    Set<GrantedAuthority> grantedAuthorities;

  }

  public static class FixedClientApiKeyServiceBuilder {

    private final Map<String,ApiKeyEntry> apiKeyEntries = new HashMap<>();

    public FixedClientApiKeyServiceBuilder addKey(String clientId, String hash, String tenantId, Set<String> permissions) {
      final ApiKeyEntry v = new ApiKeyEntry();
      v.setClientId(clientId);
      v.setHash(hash);
      v.setTenantId(tenantId);
      v.setGrantedAuthorities(permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()));
      apiKeyEntries.put(clientId, v);
      return this;
    }

    public FixedClientApiKeyService build() {
      return new FixedClientApiKeyService(apiKeyEntries);
    }
  }

  public static FixedClientApiKeyServiceBuilder builder() {
    return new FixedClientApiKeyServiceBuilder();
  }

  private FixedClientApiKeyService(Map<String, ApiKeyEntry> apiKeyEntries) {
    this.apiKeyEntries = apiKeyEntries;
  }


  private final Map<String,ApiKeyEntry> apiKeyEntries;

  @Override
  public Optional<ApiKey> findByClientId(String clientId) {
    ApiKeyEntry entry = apiKeyEntries.get(clientId);
    if (entry != null) {
      return Optional.of(ImmutableApiKey.builder()
        .clientId(entry.getClientId())
        .hash(entry.getHash())
        .owner(entry.getClientId())
        .tenantId(entry.getTenantId())
        .build());
    }
    return Optional.empty();
  }

  @Override
  public ApiKey save(ApiKey apiKey) {
    return apiKey;
  }

  @Override
  public Collection<GrantedAuthority> loadAuthorities(ApiKey apiKey) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    final ApiKeyEntry apiKeyEntry = apiKeyEntries.get(apiKey.getClientId());
    authorities.addAll(apiKeyEntry.getGrantedAuthorities());
    if (authorities.isEmpty()) {
      // If not key authorities defined fallback to default
      authorities.add(new SimpleGrantedAuthority("forms.post"));
      authorities.add(new SimpleGrantedAuthority("forms.delete"));
      authorities.add(new SimpleGrantedAuthority("forms.put"));
      authorities.add(new SimpleGrantedAuthority("forms.get"));
      authorities.add(new SimpleGrantedAuthority("questionnaires.post"));
      authorities.add(new SimpleGrantedAuthority("questionnaires.get"));
      authorities.add(new SimpleGrantedAuthority("questionnaires.put"));
      authorities.add(new SimpleGrantedAuthority("admin"));
      authorities.add(new SimpleGrantedAuthority("api"));
    }
    apiKey.getTenantId().ifPresent(tenantId -> authorities.add(ImmutableTenantGrantedAuthority.of(tenantId, tenantId)));
    return authorities;
  }

}



