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
package io.dialob.rest;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.api.rest.Errors;
import io.dialob.api.rest.ImmutableErrors;
import io.dialob.rest.type.ApiException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class RestApiExceptionMapper {

  @ExceptionHandler
  public ResponseEntity handleMethodArgumentNotValidException(@NonNull MethodArgumentNotValidException exception) {
    BindingResult bindingResult = exception.getBindingResult();
    ImmutableErrors.Builder errorsBuilder = ImmutableErrors.builder()
      .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
      .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());

    for (ObjectError objectError : bindingResult.getAllErrors()) {
      ImmutableErrors.Error.Builder builder = ImmutableErrors.Error.builder()
        .code(objectError.getCode())
        .error(objectError.getDefaultMessage());
      if (objectError instanceof FieldError) {
        FieldError fieldError = (FieldError) objectError;
        builder = builder
          .context(fieldError.getField())
          .rejectedValue(fieldError.getRejectedValue());
      }
      errorsBuilder.addErrors(builder.build());
    }
    Errors errors = errorsBuilder.build();
    HttpStatus httpStatus = resolveHttpStatus(errors);
    LOGGER.error("Invalid request ("+ httpStatus + "): " + exception.getMessage());
    return ResponseEntity.status(httpStatus).body(errors);
  }

  @ExceptionHandler
  public ResponseEntity valueInstantiationExceptionHandler(@NonNull com.fasterxml.jackson.databind.exc.ValueInstantiationException exception) {
    ImmutableErrors.Builder builder = ImmutableErrors.builder();
    Throwable cause = exception.getCause();
    String message = exception.getMessage();
    if (cause instanceof ConstraintViolationException) {
      ConstraintViolationException cve = (ConstraintViolationException) cause;
      cve.getConstraintViolations().forEach(constraintViolation -> {
        builder.addErrors(ImmutableErrors.Error.builder()
          .error(constraintViolation.getMessage())
          .rejectedValue(constraintViolation.getInvalidValue())
          .context(constraintViolation.getPropertyPath().toString())
          .build()).build();
      });
      message = cve.getConstraintViolations().stream().map(cv -> cv.getPropertyPath() + ": " + cv.getMessage()).collect(Collectors.joining("\n"));
    }
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(builder
      .message(message)
      .error(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase()).build());
  }

//  @ExceptionHandler
//  public ResponseEntity vonstraintViolationExceptionHandler(@NonNull ConstraintViolationException exception) {
//    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ImmutableErrors.builder().
//      addErrors(ImmutableErrors.Error.builder().error(exception.getMessage()).build())
//      .error(HttpStatus.BAD_REQUEST.getReasonPhrase()).build());
//  }



  @ExceptionHandler
  public ResponseEntity apiExceptionHandler(@NonNull ApiException exception) {
    Errors errors = exception.getErrors();
    HttpStatus httpStatus = resolveHttpStatus(errors);
    errors = ImmutableErrors.builder().from(errors).error(httpStatus.getReasonPhrase()).build();
    LOGGER.error("API Error ("+ httpStatus + "): " + exception.getMessage(), exception);
    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(errors);
  }

  private HttpStatus resolveHttpStatus(Errors errors) {
    Integer status = errors.getStatus();
    if (status == null) {
      LOGGER.error("No error status defined {}", errors);
      // Status should be defined on Errors
      status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
    HttpStatus httpStatus;
    try {
      httpStatus = HttpStatus.valueOf(status);
    } catch (IllegalArgumentException e) {
      LOGGER.error("Unknown error status defined {}", errors);
      httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return httpStatus;
  }
}
