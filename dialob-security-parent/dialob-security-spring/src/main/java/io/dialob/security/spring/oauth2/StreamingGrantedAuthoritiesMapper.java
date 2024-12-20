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
package io.dialob.security.spring.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Slf4j
public class StreamingGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

  private final UnaryOperator<Stream<? extends GrantedAuthority>> grantMapper;

  public StreamingGrantedAuthoritiesMapper(List<UnaryOperator<Stream<? extends GrantedAuthority>>> chain) {
    grantMapper = chain.stream()
      .reduce((streamOperator, streamOperator2) -> authoritiesStream -> streamOperator.apply(streamOperator2.apply(authoritiesStream)))
      .orElse(stream -> stream);
  }

  @Override
  public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
    var mappedAuthorities = grantMapper.apply(authorities.stream()).toList();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Mapped authorities : {}", mappedAuthorities);
    }
    return mappedAuthorities;
  }
}
