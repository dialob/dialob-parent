/*
 * nimbus-jose-jwt
 *
 * Copyright 2012-2016, Connect2id Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.dialob.security.aws.elb;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.DefaultJWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSetCache;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.*;


/**
 * Remote JSON Web Key (JWK) source specified by a JWK set URL. The retrieved
 * JWK set is cached to minimise network calls. The cache is updated whenever
 * the key selector tries to get a key with an unknown ID.
 *
 * @author Vladimir Dzhuvinov
 * @version 2018-10-28
 */
@ThreadSafe
@Slf4j
public class ElbJWKSource<C extends SecurityContext> implements JWKSource<C> {


  /**
   * The default HTTP connect timeout for JWK set retrieval, in
   * milliseconds. Set to 500 milliseconds.
   */
  public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 500;


  /**
   * The default HTTP read timeout for JWK set retrieval, in
   * milliseconds. Set to 500 milliseconds.
   */
  public static final int DEFAULT_HTTP_READ_TIMEOUT = 500;


  /**
   * The default HTTP entity size limit for JWK set retrieval, in bytes.
   * Set to 50 KBytes.
   */
  public static final int DEFAULT_HTTP_SIZE_LIMIT = 50 * 1024;


  /**
   * The JWK set URL.
   */
  private final UriTemplate jwkSetUriTemplate;


  /**
   * The JWK set cache.
   */
  private final JWKSetCache jwkSetCache;


  /**
   * The JWK set retriever.
   */
  private final ResourceRetriever jwkSetRetriever;


  /**
   * Creates a new remote JWK set using the
   * {@link DefaultResourceRetriever default HTTP resource retriever},
   * with a HTTP connect timeout set to 250 ms, HTTP read timeout set to
   * 250 ms and a 50 KByte size limit.
   *
   * @param jwkSetUriTemplate The JWK set URL. Must not be {@code null}.
   */
  public ElbJWKSource(final String jwkSetUriTemplate) {
    this(jwkSetUriTemplate, null);
  }


  /**
   * Creates a new remote JWK set.
   *
   * @param jwkSetUriTemplate The JWK set URL. Must not be {@code null}.
   * @param resourceRetriever The HTTP resource retriever to use,
   *                          {@code null} to use the
   *                          {@link DefaultResourceRetriever default
   *                          one}.
   */
  public ElbJWKSource(final String jwkSetUriTemplate,
                      final ResourceRetriever resourceRetriever) {

    this(jwkSetUriTemplate, resourceRetriever, null);
  }


  /**
   * Creates a new remote JWK set.
   *
   * @param jwkSetUriTemplate The JWK set URL. Must not be {@code null}.
   * @param resourceRetriever The HTTP resource retriever to use,
   *                          {@code null} to use the
   *                          {@link DefaultResourceRetriever default
   *                          one}.
   * @param jwkSetCache       The JWK set cache to use, {@code null} to
   *                          use the {@link com.nimbusds.jose.jwk.source.DefaultJWKSetCache default
   *                          one}.
   */
  public ElbJWKSource(final String jwkSetUriTemplate,
                      final ResourceRetriever resourceRetriever,
                      final JWKSetCache jwkSetCache) {

    if (jwkSetUriTemplate == null) {
      throw new IllegalArgumentException("The JWK set URI template must not be null");
    }
    this.jwkSetUriTemplate = new UriTemplate(jwkSetUriTemplate);

    this.jwkSetRetriever = Objects.requireNonNullElseGet(resourceRetriever, () -> new DefaultResourceRetriever(DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT, DEFAULT_HTTP_SIZE_LIMIT));

    this.jwkSetCache = Objects.requireNonNullElseGet(jwkSetCache, DefaultJWKSetCache::new);
  }


  /**
   * Updates the cached JWK set from the configured URL.
   *
   * @return The updated JWK set.
   *
   * @throws RemoteKeySourceException If JWK retrieval failed.
   */
  private JWKSet updateJWKSetFromURL(final JWKSelector jwkSelector)
    throws KeySourceException {
    Resource res;
    String kid = getFirstSpecifiedKeyID(jwkSelector.getMatcher());
    if (StringUtils.isBlank(kid)) {
      throw new KeySourceException("Couldn't retrieve remote JWK set. Could not fid a KeyId.");
    }
    URI expanded = jwkSetUriTemplate.expand(Map.of("kid", kid));
    try {
      URL url = expanded.toURL();
      LOGGER.debug("Found kid: {} and looking public key from url {}", kid, url);
      res = jwkSetRetriever.retrieveResource(url);
    } catch (IOException e) {
      throw new RemoteKeySourceException("Couldn't retrieve remote JWK set: " + e.getMessage(), e);
    }
    String publicKey = res.getContent();
    JWKSet jwkSet;
    try {
      JWK jwk = JWK.parseFromPEMEncodedObjects(publicKey);
      if (jwk instanceof RSAKey) {
        jwk = new RSAKey.Builder((RSAKey) jwk).keyID(kid).build();
      } else if (jwk instanceof com.nimbusds.jose.jwk.ECKey) {
        jwk = new com.nimbusds.jose.jwk.ECKey.Builder((com.nimbusds.jose.jwk.ECKey) jwk).keyID(kid).build();
      } else {
        throw new KeySourceException("Unknown key type: " + jwk.getKeyType());
      }
      jwkSet = new JWKSet(jwk);
    } catch (JOSEException e) {
      throw new RemoteKeySourceException("Couldn't parse remote JWK set: " + e.getMessage(), e);
    }
    jwkSetCache.put(jwkSet);
    return jwkSet;
  }


  /**
   * Returns the JWK set URL.
   *
   * @return The JWK set URL.
   */
  public UriTemplate getJWKSetUriTemplate() {

    return jwkSetUriTemplate;
  }


  /**
   * Returns the HTTP resource retriever.
   *
   * @return The HTTP resource retriever.
   */
  public ResourceRetriever getResourceRetriever() {

    return jwkSetRetriever;
  }


  /**
   * Returns the configured JWK set cache.
   *
   * @return The JWK set cache.
   */
  public JWKSetCache getJWKSetCache() {

    return jwkSetCache;
  }


  /**
   * Returns the cached JWK set.
   *
   * @return The cached JWK set, {@code null} if none or expired.
   */
  public JWKSet getCachedJWKSet() {

    return jwkSetCache.get();
  }


  /**
   * Returns the first specified key ID (kid) for a JWK matcher.
   *
   * @param jwkMatcher The JWK matcher. Must not be {@code null}.
   *
   * @return The first key ID, {@code null} if none.
   */
  protected static String getFirstSpecifiedKeyID(final JWKMatcher jwkMatcher) {

    Set<String> keyIDs = jwkMatcher.getKeyIDs();

    if (keyIDs == null || keyIDs.isEmpty()) {
      return null;
    }

    for (String id: keyIDs) {
      if (id != null) {
        return id;
      }
    }
    return null; // No kid in matcher
  }


  /**
   * {@inheritDoc} The security context is ignored.
   */
  @Override
  public List<JWK> get(final JWKSelector jwkSelector, final C context)
    throws KeySourceException {

    // Get the JWK set, may necessitate a cache update
    JWKSet jwkSet = jwkSetCache.get();
    if (jwkSetCache.requiresRefresh() || jwkSet == null) {
      try {
        // retrieve jwkSet by calling JWK set URL
        jwkSet = updateJWKSetFromURL(jwkSelector);
      } catch (Exception ex) {
        if (jwkSet == null) {
          // throw the received exception if expired.
          throw  ex;
        }
      }
    }

    // Run the selector on the JWK set
    List<JWK> matches = jwkSelector.select(jwkSet);

    if (! matches.isEmpty()) {
      // Success
      return matches;
    }

    // Refresh the JWK set if the sought key ID is not in the cached JWK set

    // Looking for JWK with specific ID?
    String soughtKeyID = getFirstSpecifiedKeyID(jwkSelector.getMatcher());
    if (soughtKeyID == null) {
      // No key ID specified, return no matches
      return Collections.emptyList();
    }

    if (jwkSet.getKeyByKeyId(soughtKeyID) != null) {
      // The key ID exists in the cached JWK set, matching
      // failed for some other reason, return no matches
      return Collections.emptyList();
    }

    // Make new HTTP GET to the JWK set URL
    jwkSet = updateJWKSetFromURL(jwkSelector);
    if (jwkSet == null) {
      // Retrieval has failed
      return Collections.emptyList();
    }

    // Repeat select, return final result (success or no matches)
    return jwkSelector.select(jwkSet);
  }
}
