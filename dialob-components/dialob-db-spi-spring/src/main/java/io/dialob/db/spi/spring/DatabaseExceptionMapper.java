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
package io.dialob.db.spi.spring;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import io.dialob.api.rest.Errors;
import io.dialob.api.rest.ImmutableErrors;
import io.dialob.db.spi.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class DatabaseExceptionMapper {

  @ExceptionHandler
  public ResponseEntity<Errors> handleDocumentNotFoundException(DocumentNotFoundException exception) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleDatabaseUnauthorizedException(DatabaseUnauthorizedException exception) {
    return buildResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleDocumentForbiddenException(DocumentForbiddenException exception) {
    return buildResponse(HttpStatus.FORBIDDEN, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleDocumentConflictException(DocumentConflictException exception) {
    return buildResponse(HttpStatus.CONFLICT, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleDocumentLockedException(DocumentLockedException exception) {
    return buildResponse(HttpStatus.LOCKED, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> invalidDefinitionException(InvalidDefinitionException exception) {
    if (exception.getCause() instanceof ConstraintViolationException constraintViolationException) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(
        ImmutableErrors.builder().error(HttpStatus.BAD_REQUEST.getReasonPhrase())
          .status(HttpStatus.BAD_REQUEST.value())
          .message(constraintViolationException.getMessage())
          .errors(constraintViolationException.getConstraintViolations().stream().<Errors.Error>map(constraintViolation -> ImmutableErrors.Error.builder()
            .error(constraintViolation.getMessage())
            .rejectedValue(constraintViolation.getInvalidValue())
            .context(constraintViolation.getPropertyPath().toString())
            .build())::iterator)
          .build()
      );

    }
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleDatabaseServiceDownException(DatabaseServiceDownException exception) {
    return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleDocumentCorruptedException(DocumentCorruptedException exception) {
    LOGGER.error("Data corrupt exception: {}", exception.getMessage(), exception);
    return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleTenantContextRequiredException(TenantContextRequiredException exception) {
    return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity<Errors> handleDatabaseException(DatabaseException exception) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
  }

  protected ResponseEntity<Errors> buildResponse(HttpStatus httpStatus, String reason) {
    LOGGER.error("Data access error ({}): {}", httpStatus.value(), reason);
    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(
      ImmutableErrors.builder().error(httpStatus.getReasonPhrase())
        .status(httpStatus.value())
        .message(reason)
      .build()
    );
  }
}
