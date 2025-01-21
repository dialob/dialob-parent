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
package io.dialob.form.service.rest;

import io.dialob.api.rest.Errors;
import io.dialob.api.rest.ImmutableErrors;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FormApiExceptionHandlersTest {

  private final FormApiExceptionHandlers formApiExceptionHandlers = new FormApiExceptionHandlers();

  /**
   * Tests the {@link FormApiExceptionHandlers#buildResponse(HttpStatus, String)} method.
   * This method is responsible for building a standardized {@link ResponseEntity} object
   * containing error information for given HTTP status and reason message.
   */

  @Test
  void testBuildResponseWithBadRequestStatus() {
    // Arrange
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String reason = "Invalid input data";

    // Act
    ResponseEntity<Errors> responseEntity = formApiExceptionHandlers.buildResponse(status, reason);

    // Assert
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    Errors errors = responseEntity.getBody();
    assertEquals(status.value(), errors.getStatus());
    assertEquals(status.getReasonPhrase(), errors.getError());
    assertEquals(reason, errors.getMessage());
  }

  @Test
  void testBuildResponseWithInternalServerErrorStatus() {
    // Arrange
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String reason = "Unexpected server error";

    // Act
    ResponseEntity<Errors> responseEntity = formApiExceptionHandlers.buildResponse(status, reason);

    // Assert
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    Errors errors = responseEntity.getBody();
    assertEquals(status.value(), errors.getStatus());
    assertEquals(status.getReasonPhrase(), errors.getError());
    assertEquals(reason, errors.getMessage());
  }

  @Test
  void testBuildResponseWithNotFoundStatus() {
    // Arrange
    HttpStatus status = HttpStatus.NOT_FOUND;
    String reason = "Resource not found";

    // Act
    ResponseEntity<Errors> responseEntity = formApiExceptionHandlers.buildResponse(status, reason);

    // Assert
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    Errors errors = responseEntity.getBody();
    assertEquals(status.value(), errors.getStatus());
    assertEquals(status.getReasonPhrase(), errors.getError());
    assertEquals(reason, errors.getMessage());
  }

  @Test
  void testBuildResponseHandlesNullReasonGracefully() {
    // Arrange
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String reason = null;

    // Act
    ResponseEntity<Errors> responseEntity = formApiExceptionHandlers.buildResponse(status, reason);

    // Assert
    assertNotNull(responseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    assertNotNull(responseEntity.getBody());
    Errors errors = responseEntity.getBody();
    assertEquals(status.value(), errors.getStatus());
    assertEquals(status.getReasonPhrase(), errors.getError());
    assertEquals(reason, errors.getMessage());
  }

}
