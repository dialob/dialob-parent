package io.dialob.spring.composer.controllers;

/*-
 * #%L
 * hdes-spring-bundle-editor
 * %%
 * Copyright (C) 2020 - 2021 Copyright 2020 ReSys OÜ
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.time.Duration;
import java.util.Collection;

import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.dialob.client.api.DialobComposer;
import io.dialob.client.api.DialobDocument.FormRevisionDocument;
import io.dialob.spring.composer.config.UiConfigBean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(UiConfigBean.REST_SPRING_CTX_PATH_EXP)
public class DialobComposerServiceController {
  
  
  private final DialobComposer composer;
  private final ObjectMapper objectMapper;
  private static final Duration timeout = Duration.ofMillis(10000);

  public DialobComposerServiceController(DialobComposer composer, ObjectMapper objectMapper, ApplicationContext ctx) {
    super();
    this.composer = composer;
    this.objectMapper = objectMapper;
    
    final var servicePath = ctx.getEnvironment().getProperty(UiConfigBean.REST_SPRING_CTX_PATH);
    
    LOGGER.info(new StringBuilder()
        .append("Dialob, Composer Service: UP").append(System.lineSeparator())
        .append("service paths:").append(System.lineSeparator())
        .append("  - ").append(servicePath).append("/models").append(": ").append("return all form revisions").append(System.lineSeparator())
        .toString());
  }

  @GetMapping(path = "/models", produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<FormRevisionDocument> models() {
    return composer.get().onItem().transform(e -> e.getRevs().values()).await().atMost(timeout);
  }

  /*

  @GetMapping(path = "/" + HdesWebConfig.EXPORTS, produces = MediaType.APPLICATION_JSON_VALUE)
  public StoreDump exports() {
    return composer.getStoreDump().await().atMost(timeout);
  }
  
  @PostMapping(path = "/" + HdesWebConfig.COMMANDS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerEntity<?> commands(@RequestBody String body) throws JsonMappingException, JsonProcessingException {
    final var command = objectMapper.readValue(body, UpdateEntity.class);
    return composer.dryRun(command).await().atMost(timeout);
  }

  @PostMapping(path = "/" + HdesWebConfig.DEBUGS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public DebugResponse debug(@RequestBody DebugRequest debug) {
    return composer.debug(debug).await().atMost(timeout);
  }

  @PostMapping(path = "/" + HdesWebConfig.IMPORTS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState importTag(@RequestBody AstTag entity) {
    return composer.importTag(entity).await().atMost(timeout);
  }

  @PostMapping(path = "/" + HdesWebConfig.RESOURCES, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState create(@RequestBody CreateEntity entity) {
    return composer.create(entity).await().atMost(timeout);
  }
  @PutMapping(path = "/" + HdesWebConfig.RESOURCES, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState update(@RequestBody UpdateEntity entity) {
    return composer.update(entity).await().atMost(timeout);
  }
  @DeleteMapping(path = "/" + HdesWebConfig.RESOURCES + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState delete(@PathVariable String id) {
    return composer.delete(id).await().atMost(timeout);
  }
  @GetMapping(path = "/" + HdesWebConfig.RESOURCES + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ComposerEntity<?> get(@PathVariable String id) {
    return composer.get(id).await().atMost(timeout);
  }
  
  @PostMapping(path = "/" + HdesWebConfig.COPYAS, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  public ComposerState copyAs(@RequestBody CopyAs entity) {
    return composer.copyAs(entity).await().atMost(timeout);
  }
  @GetMapping(path = "/" + HdesWebConfig.HISTORY + "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public HistoryEntity history(@RequestParam("id") String id) {
    return composer.getHistory(id).await().atMost(timeout);
  }
  */
}
