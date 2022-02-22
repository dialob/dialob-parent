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
package io.dialob.security.aws.elb;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class ElbJWKSourceTest {

  static String publicKeyPEM = "-----BEGIN PUBLIC KEY-----\n" +
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsX6EebeDuj7Un4A1K7IE\n" +
    "NdgeeoHTHvLnDLfbEfRRtmEY7mMxKyq/G7Gk33BdgtPSzHTg9jGEoDMoZvWLpk6b\n" +
    "Y3QHFyHp/euLPcy7HKDOT2ku3dGMOxVqv9At7YmYHE1B6Wt5a86iOqgV7sWrNZo+\n" +
    "bxuMFJu7tg2ZcmbfC3EdUQdtK/QrVuZC2ngof3bSkiPMl5itT7tDCH98TP1R2MlK\n" +
    "QboP3hOcVNppOF93dLY3VdRdptJyG7wKG2Iajaumgfv1r0METNHPvn/kFaC4+i7+\n" +
    "brzdibkd+LnF6xtI8j2BksJMtg+QQA+zVUnSMCeeAL0EbNgs7p0H0i30jNH6cUPN\n" +
    "lQIDAQAB\n" +
    "-----END PUBLIC KEY-----\n";

  static String privateKeyPEM = "-----BEGIN RSA PRIVATE KEY-----\n" +
    "MIIEowIBAAKCAQEAsX6EebeDuj7Un4A1K7IENdgeeoHTHvLnDLfbEfRRtmEY7mMx\n" +
    "Kyq/G7Gk33BdgtPSzHTg9jGEoDMoZvWLpk6bY3QHFyHp/euLPcy7HKDOT2ku3dGM\n" +
    "OxVqv9At7YmYHE1B6Wt5a86iOqgV7sWrNZo+bxuMFJu7tg2ZcmbfC3EdUQdtK/Qr\n" +
    "VuZC2ngof3bSkiPMl5itT7tDCH98TP1R2MlKQboP3hOcVNppOF93dLY3VdRdptJy\n" +
    "G7wKG2Iajaumgfv1r0METNHPvn/kFaC4+i7+brzdibkd+LnF6xtI8j2BksJMtg+Q\n" +
    "QA+zVUnSMCeeAL0EbNgs7p0H0i30jNH6cUPNlQIDAQABAoIBAEwtL3AJuehSFPEL\n" +
    "lkZVlYcCZTpQw6pRt6X2tnfDMtqiW4/cVHrhUrnxCQC4efmvLZaARxiCchhLPHjL\n" +
    "w46xx/DsGCrubD2FPPJeDqQfw8vDKdEboSKuc201TLDYz8a9xZ8HeEozmd6wyxD6\n" +
    "FxvfQhJvcTRbDjn5JjU6P1nqxxlB7kwwuWBUNfXj/HPATF9jddmNylLsvL5s5PKC\n" +
    "Q4dsDof6Zh0+3C4wvqsYnffXILHuEO55/T4f+EVoGtAymNk/Jujcaw+oEU2Q1MXL\n" +
    "BggOkTnTQhITmhy/VBBrpgof1HerVGKsCfXdTqWWu893j1URqY3QmwWqKDMFZrAx\n" +
    "CefLB2ECgYEA5DFTBHgwYt0hkqhWhilyC4AzUJ6SzQ+m65Cos23FYBJgN4Qk50SX\n" +
    "WIg7CxhwNJqc/SQQOS86aAsuQnECuk1sRRBawPAAb2xDxb5JQoz+Kk57CqhHmDte\n" +
    "I/Y2Z6BLGEymDvAGSmjVrSEqSmtdSgSIbR+u4nRLWivIJegJT5bmVN0CgYEAxx+b\n" +
    "53fI1LY6ZQWZ4E2lX7tRjuPKIELTrKfXSi+RTJBGNlG2xjOKVPE0838YYS2GcZJA\n" +
    "EohJeQVXRk1N1WztZfgw1KjQml2FAoxjMjWhGA58h6TbdDT1LYy8/iyWDVo+rmDU\n" +
    "+n+vbg2zs0pl6YcFz/amPSQ8FzMHnh4n5XNRVBkCgYBhmArWZYTkM4kRTTe8J2uk\n" +
    "KY9I0pkcZK5SH0tXegIpRZKrC7QwyPAlqBYAd9I9XUb5KPxbSKylJOsC9YxiG4zL\n" +
    "uOPnkn3NKbOOlgSFFwH5HBmZhEEihXmMzdeU2ERlvxpiVxuJpW6FniKVM3fxmSCz\n" +
    "8xH7vhsgFIuEUsnwpSwbbQKBgGplf1586LiP7j/QcFoj4vt2EawyFuyKCKS2whiH\n" +
    "tjGc3Ydkvi7Fl2Kvx8Vb4eD6/F1u8gF1BR0/N1T/NVmW7HqR1TWsRlpVIh8seEx5\n" +
    "z2wVQYERG5nKOj9udgWyOTNFRzzRzLtUwp4hgCWK2U2gsgBfIJCG7fzPbYCVSGgX\n" +
    "6BrxAoGBANcWTdxmsQuj4L53SHsYtL6kJwCgptLXh8uTA+2if0cmjcX/ielpGrR1\n" +
    "hHYZv3dAVKUDNQzR57JHdNnbT6uPQBmQTAxUv5FsFxjo8HM7SOaHaoS9czQYPeBu\n" +
    "fkqFemd3vucDcjbWdtl8ICp7aq1x7rsFqneWHNxLAYx/y8g3S5lS\n" +
    "-----END RSA PRIVATE KEY-----\n";

  static String privateKeyECPEM = "-----BEGIN EC PRIVATE KEY-----\n" +
    "MHcCAQEEIJXzBVJKyt6JvR6wibYsFQvq5aGaYcXZLgxAMaBqnFz/oAoGCCqGSM49\n" +
    "AwEHoUQDQgAE16cCiUF2CTQ74D4aZfAFVeN9i1k8MAHs86xAY/X5O7ZpSO+5uE/6\n" +
    "Vgdv9CCIpitN08Lpf1u3SIlAoLP4xKWCBg==\n" +
    "-----END EC PRIVATE KEY-----\n";


  static String publicKeyECPEM = "-----BEGIN PUBLIC KEY-----\n" +
    "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE16cCiUF2CTQ74D4aZfAFVeN9i1k8\n" +
    "MAHs86xAY/X5O7ZpSO+5uE/6Vgdv9CCIpitN08Lpf1u3SIlAoLP4xKWCBg==\n" +
    "-----END PUBLIC KEY-----";



  @Test
  public void testRSA() throws Exception {
    String jwkSetUriTemplate = "http://localhost:8080/{kid}";
    ResourceRetriever resourceRetriever = Mockito.mock(ResourceRetriever.class);
    Mockito
      .when(resourceRetriever.retrieveResource(new URL("http://localhost:8080/my-kid")))
      .thenReturn(new Resource(publicKeyPEM, "application/x-pem-file"));

    JWKMatcher matcher = new JWKMatcher.Builder()
      .keyID("my-kid")
      .build();
    JWKSelector jwkSelector = new JWKSelector(matcher);


    ElbJWKSource source = new ElbJWKSource(jwkSetUriTemplate, resourceRetriever);

    List<JWK> jwks = source.get(jwkSelector, null);

    Assertions.assertEquals(1, jwks.size());
    Assertions.assertEquals("my-kid", jwks.get(0).getKeyID());

    Mockito.verify(resourceRetriever).retrieveResource(any(URL.class));
    Mockito.verifyNoMoreInteractions(resourceRetriever);
  }
  @Test
  public void testEC() throws Exception {
    String jwkSetUriTemplate = "http://localhost:8080/{kid}";
    ResourceRetriever resourceRetriever = Mockito.mock(ResourceRetriever.class);
    Mockito
      .when(resourceRetriever.retrieveResource(new URL("http://localhost:8080/my-kid")))
      .thenReturn(new Resource(publicKeyECPEM, "application/x-pem-file"));

    JWKMatcher matcher = new JWKMatcher.Builder()
      .keyID("my-kid")
      .build();
    JWKSelector jwkSelector = new JWKSelector(matcher);


    ElbJWKSource source = new ElbJWKSource(jwkSetUriTemplate, resourceRetriever);

    List<JWK> jwks = source.get(jwkSelector, null);

    Assertions.assertEquals(1, jwks.size());
    Assertions.assertEquals("my-kid", jwks.get(0).getKeyID());

    Mockito.verify(resourceRetriever).retrieveResource(any(URL.class));
    Mockito.verifyNoMoreInteractions(resourceRetriever);
  }

}
