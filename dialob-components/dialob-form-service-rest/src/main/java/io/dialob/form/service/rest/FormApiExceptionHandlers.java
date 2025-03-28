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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FormApiExceptionHandlers {
  @ExceptionHandler
  public ResponseEntity handleInvalidRequestException(InvalidMetadataQueryException exception) {
    return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  protected ResponseEntity<Errors> buildResponse(HttpStatus httpStatus, String reason) {
    return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON).body(
      ImmutableErrors.builder().error(httpStatus.getReasonPhrase())
        .status(httpStatus.value())
        .message(reason)
        .build()
    );
  }
}
