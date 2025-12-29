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
import io.dialob.db.spi.exceptions.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseExceptionMapperTest {

  private DatabaseExceptionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new DatabaseExceptionMapper();
  }

  @Test
  void shouldHandleDocumentNotFoundException() {
    DocumentNotFoundException exception = new DocumentNotFoundException("Document not found");

    ResponseEntity<Errors> response = mapper.handleDocumentNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().getStatus());
    assertEquals("Not Found", response.getBody().getError());
    assertEquals("Document not found", response.getBody().getMessage());
  }

  @Test
  void shouldHandleDatabaseUnauthorizedException() {
    DatabaseUnauthorizedException exception = new DatabaseUnauthorizedException("Unauthorized access");

    ResponseEntity<Errors> response = mapper.handleDatabaseUnauthorizedException(exception);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(401, response.getBody().getStatus());
    assertEquals("Unauthorized", response.getBody().getError());
    assertEquals("Unauthorized access", response.getBody().getMessage());
  }

  @Test
  void shouldHandleDocumentForbiddenException() {
    DocumentForbiddenException exception = new DocumentForbiddenException("Access forbidden");

    ResponseEntity<Errors> response = mapper.handleDocumentForbiddenException(exception);

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(403, response.getBody().getStatus());
    assertEquals("Forbidden", response.getBody().getError());
    assertEquals("Access forbidden", response.getBody().getMessage());
  }

  @Test
  void shouldHandleDocumentConflictException() {
    DocumentConflictException exception = new DocumentConflictException("Document conflict");

    ResponseEntity<Errors> response = mapper.handleDocumentConflictException(exception);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(409, response.getBody().getStatus());
    assertEquals("Conflict", response.getBody().getError());
    assertEquals("Document conflict", response.getBody().getMessage());
  }

  @Test
  void shouldHandleDocumentLockedException() {
    DocumentLockedException exception = new DocumentLockedException("Document is locked");

    ResponseEntity<Errors> response = mapper.handleDocumentLockedException(exception);

    assertEquals(HttpStatus.LOCKED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(423, response.getBody().getStatus());
    assertEquals("Locked", response.getBody().getError());
    assertEquals("Document is locked", response.getBody().getMessage());
  }

  @Test
  void shouldHandleInvalidDefinitionExceptionWithoutConstraintViolation() {
    InvalidDefinitionException exception = mock(InvalidDefinitionException.class);
    when(exception.getMessage()).thenReturn("Invalid definition");
    when(exception.getCause()).thenReturn(null);

    ResponseEntity<Errors> response = mapper.invalidDefinitionException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("Bad Request", response.getBody().getError());
    assertEquals("Invalid definition", response.getBody().getMessage());
  }

  @Test
  void shouldHandleInvalidDefinitionExceptionWithConstraintViolation() {
    // Create mock constraint violation
    @SuppressWarnings("unchecked")
    ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
    Path propertyPath = mock(Path.class);
    when(propertyPath.toString()).thenReturn("fieldName");
    when(violation.getMessage()).thenReturn("Field cannot be null");
    when(violation.getInvalidValue()).thenReturn(null);
    when(violation.getPropertyPath()).thenReturn(propertyPath);

    ConstraintViolationException constraintException = new ConstraintViolationException(
      "Validation failed",
      Set.of(violation)
    );

    InvalidDefinitionException exception = mock(InvalidDefinitionException.class);
    when(exception.getCause()).thenReturn(constraintException);

    ResponseEntity<Errors> response = mapper.invalidDefinitionException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("Bad Request", response.getBody().getError());
    assertTrue(response.getBody().getMessage().contains("Validation failed"));

    // Verify constraint violation details are included
    assertNotNull(response.getBody().getErrors());
    assertTrue(response.getBody().getErrors().iterator().hasNext());
    Errors.Error error = response.getBody().getErrors().getFirst();
    assertEquals("Field cannot be null", error.getError());
    assertEquals("fieldName", error.getContext());
  }

  @Test
  void shouldHandleDatabaseServiceDownException() {
    DatabaseServiceDownException exception = new DatabaseServiceDownException("Service unavailable");

    ResponseEntity<Errors> response = mapper.handleDatabaseServiceDownException(exception);

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(503, response.getBody().getStatus());
    assertEquals("Service Unavailable", response.getBody().getError());
    assertEquals("Service unavailable", response.getBody().getMessage());
  }

  @Test
  void shouldHandleDocumentCorruptedException() {
    DocumentCorruptedException exception = new DocumentCorruptedException("Document corrupted");

    ResponseEntity<Errors> response = mapper.handleDocumentCorruptedException(exception);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(422, response.getBody().getStatus());
    assertEquals("Unprocessable Entity", response.getBody().getError());
    assertEquals("Document corrupted", response.getBody().getMessage());
  }

  @Test
  void shouldHandleTenantContextRequiredException() {
    TenantContextRequiredException exception = new TenantContextRequiredException("Tenant context required");

    ResponseEntity<Errors> response = mapper.handleTenantContextRequiredException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().getStatus());
    assertEquals("Not Found", response.getBody().getError());
    assertEquals("Tenant context required", response.getBody().getMessage());
  }

  @Test
  void shouldHandleDatabaseException() {
    DatabaseException exception = new DatabaseException("Database error");

    ResponseEntity<Errors> response = mapper.handleDatabaseException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(500, response.getBody().getStatus());
    assertEquals("Internal Server Error", response.getBody().getError());
    assertEquals("Database error", response.getBody().getMessage());
  }

  @Test
  void shouldBuildResponseWithCorrectContentType() {
    DocumentNotFoundException exception = new DocumentNotFoundException("Test");

    ResponseEntity<Errors> response = mapper.handleDocumentNotFoundException(exception);

    assertEquals("application/json", response.getHeaders().getContentType().toString());
  }

  @Test
  void shouldHandleExceptionsWithNullMessages() {
    DocumentNotFoundException exception = new DocumentNotFoundException(null);

    ResponseEntity<Errors> response = mapper.handleDocumentNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNull(response.getBody().getMessage());
  }

  @Test
  void shouldHandleExceptionsWithEmptyMessages() {
    DocumentNotFoundException exception = new DocumentNotFoundException("");

    ResponseEntity<Errors> response = mapper.handleDocumentNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("", response.getBody().getMessage());
  }

  @Test
  void shouldHandleMultipleConstraintViolations() {
    // Create multiple mock constraint violations
    @SuppressWarnings("unchecked")
    ConstraintViolation<Object> violation1 = mock(ConstraintViolation.class);
    Path path1 = mock(Path.class);
    when(path1.toString()).thenReturn("field1");
    when(violation1.getMessage()).thenReturn("Error 1");
    when(violation1.getInvalidValue()).thenReturn("invalid1");
    when(violation1.getPropertyPath()).thenReturn(path1);

    @SuppressWarnings("unchecked")
    ConstraintViolation<Object> violation2 = mock(ConstraintViolation.class);
    Path path2 = mock(Path.class);
    when(path2.toString()).thenReturn("field2");
    when(violation2.getMessage()).thenReturn("Error 2");
    when(violation2.getInvalidValue()).thenReturn("invalid2");
    when(violation2.getPropertyPath()).thenReturn(path2);

    ConstraintViolationException constraintException = new ConstraintViolationException(
      "Multiple validation errors",
      Set.of(violation1, violation2)
    );

    InvalidDefinitionException exception = mock(InvalidDefinitionException.class);
    when(exception.getCause()).thenReturn(constraintException);

    ResponseEntity<Errors> response = mapper.invalidDefinitionException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getErrors());

    // Verify we have multiple errors
    long count = response.getBody().getErrors().spliterator().getExactSizeIfKnown();
    assertEquals(2, count);
  }

  @Test
  void shouldHandleExceptionsWithLongMessages() {
    String longMessage = "A".repeat(1000);
    DocumentNotFoundException exception = new DocumentNotFoundException(longMessage);

    ResponseEntity<Errors> response = mapper.handleDocumentNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(longMessage, response.getBody().getMessage());
  }

  @Test
  void shouldHandleExceptionsWithSpecialCharacters() {
    String messageWithSpecialChars = "Error: <>&\"'";
    DocumentNotFoundException exception = new DocumentNotFoundException(messageWithSpecialChars);

    ResponseEntity<Errors> response = mapper.handleDocumentNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(messageWithSpecialChars, response.getBody().getMessage());
  }
}
