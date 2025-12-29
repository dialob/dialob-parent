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
package io.dialob.api.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = Response.ResposeRecord.class)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public interface Response extends ResponseStatus {

  Response OK_RESPONSE = new ResposeRecord(true, null, null);

  Response NOT_OK_RESPONSE = new ResposeRecord(false, null, null);

  String getError();

  String getReason();

  record ResposeRecord(boolean ok, String error, String reason) implements Response {
    @Override
    public String getError() {
      return error;
    }

    @Override
    public String getReason() {
      return reason;
    }
  }

}
