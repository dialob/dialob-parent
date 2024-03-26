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
package io.dialob.questionnaire.service;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.dialob.integration.api.event.Event;
import io.dialob.integration.api.event.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.task.TaskExecutor;

public class QuestionnaireSessionEventPublisher implements EventPublisher {

  private final TaskExecutor taskExecutor;

  private final ApplicationEventPublisher delegate;

  public QuestionnaireSessionEventPublisher(TaskExecutor taskExecutor, ApplicationEventPublisher delegate) {
    this.taskExecutor = taskExecutor;
    this.delegate = delegate;
  }

  @Override
  public void publish(@NonNull Event event) {
    taskExecutor.execute(() -> delegate.publishEvent(event));

  }
}
