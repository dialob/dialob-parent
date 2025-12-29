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

import io.dialob.api.rest.Errors;
import io.dialob.rest.type.ApiException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestApiExceptionMapperTest {

  @Test
  void shouldReturnStatus500WhenErrorsDoNotDefinedIt() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    ResponseEntity entity = mapper.apiExceptionHandler(new ApiException(new Errors.Builder().build()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    Errors errorsOut = (Errors) entity.getBody();
    assertEquals("Internal Server Error", errorsOut.getError());
    assertNull(errorsOut.getMessage());
    assertNotNull(errorsOut.getTimestamp());
    assertNull(errorsOut.getTrace());
    assertNull(errorsOut.getPath());
    assertNull(errorsOut.getErrors());
  }

  @Test
  void shouldReturnStatus500WhenErrorsDefinesUnknownStatus() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    ResponseEntity entity = mapper.apiExceptionHandler(new ApiException(new Errors.Builder().status(999).build()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
    Errors errorsOut = (Errors) entity.getBody();
    assertEquals("Internal Server Error", errorsOut.getError());
    assertNull(errorsOut.getMessage());
    assertNotNull(errorsOut.getTimestamp());
    assertNull(errorsOut.getTrace());
    assertNull(errorsOut.getPath());
    assertNull(errorsOut.getErrors());
  }

  @Test
  void shouldReturnErrorWithoutDetails() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    MethodArgumentNotValidException exception = mock();
    BindingResult bindingResult = mock();
    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getAllErrors()).thenReturn(List.of());
    var entity = mapper.handleMethodArgumentNotValidException(exception);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, entity.getStatusCode());
    Errors errorsResult = (Errors) entity.getBody();
    assertEquals(new Errors.Builder()
      .timestamp(errorsResult.getTimestamp())
      .status(422).error("Unprocessable Entity").build(), errorsResult);

    Mockito.verify(exception, Mockito.times(1)).getBindingResult();
    Mockito.verify(exception, Mockito.times(1)).getMessage();
    Mockito.verify(bindingResult, Mockito.times(1)).getAllErrors();
    Mockito.verifyNoMoreInteractions(exception);
  }

  @Test
  void shouldReturnErrorWithDetails() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    MethodArgumentNotValidException exception = mock();
    BindingResult bindingResult = mock();
    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getAllErrors()).thenReturn(List.of(new ObjectError("objectName", "defaultMessage")));
    var entity = mapper.handleMethodArgumentNotValidException(exception);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, entity.getStatusCode());
    Errors errorsResult = (Errors) entity.getBody();
    assertEquals(new Errors.Builder()
      .timestamp(errorsResult.getTimestamp())
      .addErrors(new Errors.Error.Builder()
        .error("defaultMessage")
        .build())
      .status(422).error("Unprocessable Entity").build(), errorsResult);

    Mockito.verify(exception, Mockito.times(1)).getBindingResult();
    Mockito.verify(exception, Mockito.times(1)).getMessage();
    Mockito.verify(bindingResult, Mockito.times(1)).getAllErrors();
    Mockito.verifyNoMoreInteractions(exception);
  }

  @Test
  void shouldConvertInstantiationExceptionHandlerWithoutConstrainViolations() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    com.fasterxml.jackson.databind.exc.ValueInstantiationException exception = mock();
    var entity = mapper.valueInstantiationExceptionHandler(exception);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, entity.getStatusCode());
    Errors errorsResult = (Errors) entity.getBody();
    assertEquals(new Errors.Builder()
      .timestamp(errorsResult.getTimestamp())
      .status(422).error("Unprocessable Entity").build(), errorsResult);

    Mockito.verify(exception, Mockito.times(1)).getCause();
    Mockito.verify(exception, Mockito.times(1)).getMessage();
    Mockito.verifyNoMoreInteractions(exception);
  }


  @Test
  void shouldConvertInstantiationExceptionHandlerWithConstrainViolations() {
    RestApiExceptionMapper mapper = new RestApiExceptionMapper();
    com.fasterxml.jackson.databind.exc.ValueInstantiationException exception = mock();
    ConstraintViolationException cvexception = mock();
    when(exception.getCause()).thenReturn(cvexception);
    ConstraintViolation cv = mock();
    when(cvexception.getConstraintViolations()).thenReturn(Set.of(cv));
    when(cv.getMessage()).thenReturn("message");
    when(cv.getInvalidValue()).thenReturn("invalidValue");
    when(cv.getPropertyPath()).thenReturn(new Path() {
      @Override
      public Iterator<Node> iterator() {
        return Collections.emptyIterator();
      }

      @Override
      public String toString() {
        return "propertyPath";
      }
    });

    var entity = mapper.valueInstantiationExceptionHandler(exception);

    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, entity.getStatusCode());
    Errors errorsResult = (Errors) entity.getBody();
    assertEquals(new Errors.Builder()
      .timestamp(errorsResult.getTimestamp())
      .message("propertyPath: message")
      .status(422).error("Unprocessable Entity")
      .addErrors(new Errors.Error.Builder()
        .context("propertyPath")
        .error("message")
        .rejectedValue("invalidValue")
        .build())
      .build(), errorsResult);

    Mockito.verify(exception, Mockito.times(1)).getCause();
    Mockito.verify(exception, Mockito.times(1)).getMessage();
    Mockito.verify(cv, Mockito.times(1)).getInvalidValue();
    Mockito.verify(cv, Mockito.times(2)).getMessage();
    Mockito.verify(cv, Mockito.times(2)).getPropertyPath();
    Mockito.verifyNoMoreInteractions(exception,cv);
  }


}
