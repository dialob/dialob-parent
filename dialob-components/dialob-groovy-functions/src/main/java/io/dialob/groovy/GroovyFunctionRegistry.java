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
package io.dialob.groovy;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import io.dialob.rule.parser.function.FunctionRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GroovyFunctionRegistry {

  private final FunctionRegistry functionRegistry;

  private List<String> groovyFunctions = new ArrayList<>();

  private final GroovyClassLoader groovyClassLoader;

  private final ApplicationContext applicationContext;

  private String codePackage = "io.dialob.groovy";

  public GroovyFunctionRegistry(ApplicationContext applicationContext, FunctionRegistry functionRegistry) {
    this(applicationContext, functionRegistry, Thread.currentThread().getContextClassLoader());
  }

  public GroovyFunctionRegistry(ApplicationContext applicationContext, FunctionRegistry functionRegistry, ClassLoader classLoader) {
    this(applicationContext, functionRegistry, new GroovyClassLoader(classLoader));
  }

  public GroovyFunctionRegistry(ApplicationContext applicationContext, FunctionRegistry functionRegistry, GroovyClassLoader groovyClassLoader) {
    this.applicationContext = applicationContext;
    this.functionRegistry = functionRegistry;
    this.groovyClassLoader = new GroovyClassLoader(Thread.currentThread().getContextClassLoader());
  }

  public void setGroovyFunctions(List<String> groovyFunctions) {
    this.groovyFunctions = groovyFunctions;
  }

  private void compileScript(@NonNull Resource resource, String codePackage) {
    LOGGER.info("Compiling groovy resource {}", resource);
    try {
      String externalForm = resource.getURL().toExternalForm();
      LOGGER.debug("Resource location: {}", externalForm);
      GroovyCodeSource codeSource = new GroovyCodeSource(new InputStreamReader(resource.getInputStream()), externalForm, codePackage);
      Class<?> groovyClass = groovyClassLoader.parseClass(codeSource);
      for (Method method : groovyClass.getMethods()) {
        if (Modifier.isStatic(method.getModifiers())) {
          DialobDDRLFunction asyncAnnotation = method.getAnnotation(DialobDDRLFunction.class);
          if (asyncAnnotation != null) {
            if(LOGGER.isDebugEnabled()) {
              LOGGER.debug(" - registering {} function: {}.{} as {}", asyncAnnotation.async() ? "async" : "sync", groovyClass.getName(), method.getName(), method.getName());
            }
            functionRegistry.configureFunction(method.getName(), groovyClass, asyncAnnotation.async());
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
      // TODO: Currently groovy compilation errors fail backend to start. Might not be desired behavior in case of
      // dynamically configured groovy-functions
    }
  }

  @PostConstruct
  void registerGroovyFunctions() {
    groovyFunctions.forEach((resource) -> {
      compileScript(applicationContext.getResource(resource), this.codePackage);
    });
  }
}
