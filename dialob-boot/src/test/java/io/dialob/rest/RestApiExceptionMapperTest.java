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
package io.dialob.rest;

import io.dialob.api.rest.ImmutableErrors;
import io.dialob.rest.type.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class RestApiExceptionMapperTest {

  @Test
  public void shouldReturnStatus500WhenErrorsDoNotDefinedIt() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    ResponseEntity entity = mapper.apiExceptionHandler(new ApiException(ImmutableErrors.builder().build()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    ImmutableErrors errorsOut = (ImmutableErrors) entity.getBody();
    assertEquals("Internal Server Error", errorsOut.getError());
    assertNull(errorsOut.getMessage());
    assertNotNull(errorsOut.getTimestamp());
    assertNull(errorsOut.getTrace());
    assertNull(errorsOut.getPath());
    assertNull(errorsOut.getErrors());
  }

  @Test
  public void shouldReturnStatus500WhenErrorsDefinesUnknownStatus() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    ResponseEntity entity = mapper.apiExceptionHandler(new ApiException(ImmutableErrors.builder().status(999).build()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    ImmutableErrors errorsOut = (ImmutableErrors) entity.getBody();
    assertEquals("Internal Server Error", errorsOut.getError());
    assertNull(errorsOut.getMessage());
    assertNotNull(errorsOut.getTimestamp());
    assertNull(errorsOut.getTrace());
    assertNull(errorsOut.getPath());
    assertNull(errorsOut.getErrors());
  }

}
