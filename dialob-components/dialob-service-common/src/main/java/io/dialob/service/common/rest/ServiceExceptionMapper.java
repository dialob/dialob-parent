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
package io.dialob.service.common.rest;

import io.dialob.service.common.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServiceExceptionMapper {

  @ExceptionHandler
  public ResponseEntity serviceException(NotFoundServiceException exception) {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler
  public ResponseEntity serviceException(AccessDeniedServiceException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler
  public ResponseEntity serviceException(UpdateConflictServiceException exception) {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler
  public ResponseEntity serviceException(ServiceDownServiceException exception) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
  }

  @ExceptionHandler
  public ResponseEntity serviceException(InvalidCallServiceException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }
}

