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
package io.dialob.db.mongo;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public class MongoQuestionnaireIdObfuscator {

  private byte[] toBytes(String id) {
    try {
      if (id.length() % 2 == 1) {
        id = "0" + id;
      }
      return Hex.decodeHex(id.toCharArray());
    } catch (DecoderException e) {
      return null;
    }
  }

  private String toHex(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    return new String(Hex.encodeHex(bytes, true));
  }


  public String toPublicId(String id) {
    if (id == null) {
      return null;
    }
    final byte[] bytes = toBytes(id);
    if (bytes == null) {
      return null;
    }
    return toHex(encrypt(bytes));
  }

  public String toMongoId(String id) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    final byte[] bytes = toBytes(id);
    if (bytes == null) {
      return null;
    }
    return toHex(decrypt(bytes));
  }

  private byte[] encrypt(byte[] bytes) {
    return bytes;
  }

  private byte[] decrypt(byte[] bytes) {
    return bytes;
  }
}
