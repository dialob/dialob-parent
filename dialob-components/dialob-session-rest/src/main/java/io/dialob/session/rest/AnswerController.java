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
package io.dialob.session.rest;

import io.dialob.api.proto.Actions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("${dialob.session.rest.context:/}")
@RestController
public interface AnswerController {

  @GetMapping("/{sessionId}")
  ResponseEntity<Actions> getState(@PathVariable("sessionId") String sessionId);

  @PostMapping("/{sessionId}")
  ResponseEntity<Actions> answers(@PathVariable("sessionId") String sessionId, @RequestBody Actions actions);
}
