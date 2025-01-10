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
/*
Generates Dialob REST API key tokens

Requires:
  uuid-parse

Usage:
  node apikeygen.js tenantId apiKeySalt apiKeyHash

  apiKeySalt - API Key Secret Salt (from dialob.api.apiKeySalt)
  clientId - client ID in UUID string format: 00000000-0000-0000-0000-000000000000 (optional)
  secret - Base64 encoded API key secret part (optional)

  Outputs API key token in base64 format to use in x-api-key HTTP headers
  and base64 encoded hash value for `dialob.api.apiKeys.*.hash`
*/
const uuid = require('uuid');
const crypto = require('crypto');

const apiKeySalt = process.argv[2];

var decodedSecret

var clientIdBytes;
var clientId;
if (process.argv.length > 3) {
  clientId = uuid.parse(process.argv[3])
} else {
  clientIdBytes = Buffer.allocUnsafe(16)
  clientId = uuid.v4(undefined,clientIdBytes)
}
clientIdBytes = Buffer.from(clientId);

var secret;
if (process.argv.length > 4) {
  decodedSecret = Buffer.from(process.argv[4], 'base64');
} else {
  decodedSecret = crypto.randomBytes(14)
}

const hmac = crypto.createHmac('sha256', Buffer.from(apiKeySalt));
hmac.update(decodedSecret);

if (decodedSecret.length != 14) {
  console.warn(`secret is ${decodedSecret.length} bytes. Should be exactly 14 bytes`)
}

const tokenBytes = Buffer.allocUnsafe(30);

clientIdBytes.copy(tokenBytes, 0, 0, 16);
decodedSecret.copy(tokenBytes, 16, 0, 14);

console.log(`clientId:  ${uuid.stringify(clientId)}`)
console.log(`hash:      ${hmac.digest().toString('base64')}`)
console.log(`x-api-key: ${tokenBytes.toString('base64')}`);

