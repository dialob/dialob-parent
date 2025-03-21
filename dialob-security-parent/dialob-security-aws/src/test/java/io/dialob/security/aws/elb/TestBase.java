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
package io.dialob.security.aws.elb;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class TestBase {


  KeyPair createKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    return kpg.generateKeyPair();
  }

  String accesstoken(Instant now, KeyPair kp) throws JOSEException {
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
      .subject("00000000-0000-0000-0000-000000000000")
      .issueTime(new Date(now.toEpochMilli()))
      .issuer("https://example.com/authority")
      .expirationTime(new Date(now.plus(5, ChronoUnit.MINUTES).toEpochMilli()))
      .jwtID("00000000-0000-0000-0000-000000000001")
      .claim("cognito:groups", List.of("admin"))
      .claim("token_use", "access")
      .claim("scope", "openid")
      .claim("version", 2)
      .claim("client_id", "clientii")
      .claim("username", "00000000-0000-0000-0000-000000000000")
      .claim("auth_time", new Date(now.minus(5, ChronoUnit.MINUTES).toEpochMilli()))
      .build();

    com.nimbusds.jose.jwk.RSAKey key = new com.nimbusds.jose.jwk.RSAKey.Builder((RSAPublicKey) kp.getPublic())
       	.keyUse(KeyUse.SIGNATURE)
       	.keyID("1")
       	.build();

    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
      .keyID("authentication-key")
      .build();
    RSASSASigner signer = new RSASSASigner(kp.getPrivate());
    SignedJWT jwt = new SignedJWT(header, claimsSet);
    jwt.sign(signer);
    return jwt.serialize();
  }

  String idToken(Instant now, KeyPair kp) throws JOSEException {

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
    .claim("email_verified", "True")
    .claim("given_name", "Hemmo")
    .claim("family_name", "Hbo")
    .claim("email", "hemmo.hbo@example.com")
    .claim("exp", new Date(now.plus(5, ChronoUnit.MINUTES).toEpochMilli()))
    .claim("username", "00000000-0000-0000-0000-000000000002")
    .subject("00000000-0000-0000-0000-000000000002")
    .issuer("https://example.com/authority")
    .expirationTime(new Date(now.plus(5, ChronoUnit.MINUTES).toEpochMilli()))
    .build();

    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
      .keyID("authentication-key")
      .type(JOSEObjectType.JWT)
      .customParam("client", "clientii")
      .customParam("signer", "arn:aws:elasticloadbalancing:us-east-1:00000000000:loadbalancer/app/xxx/yyy")
      .customParam("exp", now.plus(5, ChronoUnit.MINUTES).toEpochMilli() / 1000)
      .customParam("iss","https://example.com/authority")
      .build();


    RSASSASigner signer = new RSASSASigner(kp.getPrivate());
    SignedJWT jwt = new SignedJWT(header, claimsSet);
    jwt.sign(signer);
    return jwt.serialize();
  }

}
