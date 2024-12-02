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

import io.dialob.function.DialobFunctionAutoConfiguration;
import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.api.VariableNotDefinedException;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.security.tenant.CurrentTenant;
import io.dialob.security.tenant.Tenant;
import io.dialob.security.tenant.TenantContextHolderCurrentTenant;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
  DialobFunctionAutoConfiguration.class,
  GroovyFunctionRegistryTest.TestConfiguration.class
})
public class GroovyFunctionRegistryTest {

  @Configuration(proxyBeanMethods = false)
  public static class TestConfiguration {

    @Bean
    public GroovyFunctionRegistry groovyFunctionRegistry(ApplicationContext appCtx, FunctionRegistry functionRegistry) {
      GroovyFunctionRegistry gfr = new GroovyFunctionRegistry(appCtx, functionRegistry);
      List<String> functions = new ArrayList<>();
      functions.add("classpath:/scripts/test.groovy");
      functions.add("file:src/test/resources/scripts/mapping.groovy");
      gfr.setGroovyFunctions(functions);
      return gfr;
    }

    @Bean
    public CurrentTenant currentTenant() {
      return TenantContextHolderCurrentTenant.INSTANCE;
    }
  }

  @Inject
  private GroovyFunctionRegistry groovyFunctionRegistry;

  @Inject
  private FunctionRegistry functionRegistry;

  @Test
  public void testGroovyRegistry() throws VariableNotDefinedException, ClassNotFoundException {
    assertNotNull(groovyFunctionRegistry);
    assertEquals(ValueType.STRING, functionRegistry.returnTypeOf("testFunction", ValueType.STRING));
    TenantContextHolderCurrentTenant.runInTenantContext(Tenant.of("test"), () -> {
      FunctionRegistry.FunctionCallback callback = Mockito.mock(FunctionRegistry.FunctionCallback.class);
      functionRegistry.invokeFunctionAsync(callback, "Test.testFunction", "blah");
      verify(callback).succeeded("blah blah");
      functionRegistry.invokeFunction(callback, "Mapping.mappingFunction", "value1");
      verify(callback).succeeded("4");
      verifyNoMoreInteractions(callback);
    });
  }

}
