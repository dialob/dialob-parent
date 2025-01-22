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

/**
 * A controller advice class that maps database-related exceptions to appropriate HTTP responses.
 * This class is used to centralize exception handling and provide consistent responses for various
 * database-related issues.
 *
 * The class defines multiple exception handlers for specific exceptions, returning corresponding
 * HTTP status codes and error messages. The error responses are structured using the {@code Errors}
 * class, ensuring a standard format for API responses.
 *
 * Exception Handling:
 * <table>
 *   <caption>Mapping of exceptions to HTTP statuses</caption>
 *   <thead>
 *     <tr>
 *       <th>Exception</th>
 *       <th>HTTP Status</th>
 *       <th>Description</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>{@link DocumentNotFoundException}</td>
 *       <td>404 (Not Found)</td>
 *       <td>Resource not found.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link DatabaseUnauthorizedException}</td>
 *       <td>401 (Unauthorized)</td>
 *       <td>Access unauthorized.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link DocumentForbiddenException}</td>
 *       <td>403 (Forbidden)</td>
 *       <td>Access forbidden.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link DocumentConflictException}</td>
 *       <td>409 (Conflict)</td>
 *       <td>Request conflict with the current state.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link DocumentLockedException}</td>
 *       <td>423 (Locked)</td>
 *       <td>Resource is locked.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link InvalidDefinitionException}, {@link ConstraintViolationException}</td>
 *       <td>400 (Bad Request)</td>
 *       <td>Invalid data format or validation errors.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link DatabaseServiceDownException}</td>
 *       <td>503 (Service Unavailable)</td>
 *       <td>Database service is unavailable.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link DocumentCorruptedException}</td>
 *       <td>422 (Unprocessable Entity)</td>
 *       <td>Resource is corrupted; logs exception details.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link TenantContextRequiredException}</td>
 *       <td>404 (Not Found)</td>
 *       <td>Specific tenant context is required.</td>
 *     </tr>
 *     <tr>
 *       <td>{@link DatabaseException}</td>
 *       <td>500 (Internal Server Error)</td>
 *       <td>Unhandled database error.</td>
 *     </tr>
 *   </tbody>
 * </table>
 *
 * <h3>Error Response:</h3>
 * The {@code buildResponse} method constructs the error response using the provided HTTP status code
 * and error message. The error details are encapsulated within an {@code Errors} object. For validation
 * errors, additional error details may be provided.
 *
 * Example JSON output:
 * <pre>
 * {
 *   "error": "Bad Request",
 *   "status": 400,
 *   "message": "Invalid data format",
 *   "errors": [
 *     {
 *       "error": "Field cannot be null",
 *       "rejectedValue": null,
 *       "context": "fieldName"
 *     }
 *   ]
 * }
 * </pre>
 *
 * <h3>Usage:</h3>
 * The class is annotated with {@code @ControllerAdvice} to enable its use as a global exception
 * handler across the application.
 */
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
