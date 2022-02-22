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
package io.dialob.boot;

import io.dialob.api.rest.ImmutableErrors;
import io.dialob.session.engine.DialobProgramBuildException;
import io.dialob.session.engine.program.ProgramBuilderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DialobProgramExceptionHandlers {

  @ExceptionHandler
  public ResponseEntity handleProgramBuilderException(ProgramBuilderException e) {
    return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
  }

  @ExceptionHandler
  public ResponseEntity handleDialobProgramBuildException(DialobProgramBuildException e) {
    return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
  }

  private ResponseEntity buildResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(ImmutableErrors.builder()
      .status(status.value())
      .error(status.getReasonPhrase())
      .message(message)
      .build()
    );
  }
}
