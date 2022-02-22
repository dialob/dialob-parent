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
package io.dialob.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;

import io.dialob.rule.parser.api.ValueType;
import io.dialob.rule.parser.api.VariableNotDefinedException;
import io.dialob.rule.parser.function.DefaultFunctions;
import io.dialob.rule.parser.function.FunctionRegistry;
import io.dialob.rule.parser.function.FunctionRegistryException;
import io.dialob.security.tenant.CurrentTenant;

public class FunctionRegistryImplTest {

  public static Boolean testFunction1() { return null; }

  @Test
  public void shouldRegisterFunction() throws Exception {
    TaskExecutor taskExecutor = Mockito.mock(TaskExecutor.class);
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    FunctionRegistry functionRegistry = new FunctionRegistryImpl(taskExecutor, currentTenant);
    functionRegistry.configureFunction("testFunction1", FunctionRegistryImplTest.class, false);
    assertEquals(ValueType.BOOLEAN ,functionRegistry.returnTypeOf("testFunction1"));
    assertFalse(functionRegistry.isAsyncFunction("testFunction1"));
  }


  @Test
  public void shouldThrowExceptionIfFunctionDoNotExists() {
    Assertions.assertThrows(FunctionRegistryException.class, () -> {
      TaskExecutor taskExecutor = Mockito.mock(TaskExecutor.class);
      CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
      FunctionRegistry functionRegistry = new FunctionRegistryImpl(taskExecutor, currentTenant);
      functionRegistry.configureFunction("notExists", FunctionRegistryImplTest.class, false);
    });
  }


  public static Boolean isThisOk(String thisone) {
    return false;
  }


  public static Boolean isThisOk2() {
    return false;
  }

  public static String findThing(String value, String value2) {
    return null;
  }


  @Test
  public void shouldRegisterSimpleStaticMethodAsFunction() throws Exception {
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    FunctionRegistry functionRegistry = new FunctionRegistryImpl(Mockito.mock(TaskExecutor.class), currentTenant);
    functionRegistry.configureFunction("isThisOk", FunctionRegistryImplTest.class, false);
    assertEquals(ValueType.BOOLEAN, functionRegistry.returnTypeOf("isThisOk", ValueType.STRING));
  }

  @Test
  public void shouldRegisterSimpleStaticMethodAsFunction2() throws Exception {
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    FunctionRegistry functionRegistry = new FunctionRegistryImpl(Mockito.mock(TaskExecutor.class), currentTenant);
    functionRegistry.configureFunction("isThisOk2", FunctionRegistryImplTest.class, false);
    assertEquals(ValueType.BOOLEAN, functionRegistry.returnTypeOf("isThisOk2"));
  }

  @Test
  public void shouldRegisterSimpleStaticMethodAsFunction3() throws Exception {
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    FunctionRegistry functionRegistry = new FunctionRegistryImpl(Mockito.mock(TaskExecutor.class), currentTenant);
    functionRegistry.configureFunction("findThing", FunctionRegistryImplTest.class, false);
    assertEquals(ValueType.STRING, functionRegistry.returnTypeOf("findThing", ValueType.STRING, ValueType.STRING));
  }


  @Test
  public void shouldRegisterDateFunctions() throws Exception {
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    FunctionRegistry functionRegistry = new FunctionRegistryImpl(Mockito.mock(TaskExecutor.class), currentTenant);
    functionRegistry.configureFunction("today", "now", LocalDate.class, false);
    functionRegistry.configureFunction("now", "now", LocalTime.class, false);

    assertEquals(ValueType.TIME, functionRegistry.returnTypeOf("now"));

    assertEquals(ValueType.DATE, functionRegistry.returnTypeOf("today"));
  }


  @Test
  public void shouldReportIfExecutionIsRejected() {
    TaskExecutor taskExecutor = Mockito.mock(TaskExecutor.class);
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    FunctionRegistry.FunctionCallback callback = Mockito.mock(FunctionRegistry.FunctionCallback.class);
    FunctionRegistry functionRegistry = new FunctionRegistryImpl(taskExecutor, currentTenant);
    Mockito.doThrow(new TaskRejectedException("error")).when(taskExecutor).execute(any(Runnable.class));
    functionRegistry.invokeFunctionAsync(callback, "mock");
    Mockito.verify(callback).failed("error");
  }

  @Test
  public void testFunctionRegistering() throws Exception {
    CurrentTenant currentTenant = Mockito.mock(CurrentTenant.class);
    FunctionRegistry functionRegistry = new FunctionRegistryImpl(Mockito.mock(TaskExecutor.class), currentTenant);
    DefaultFunctions defaultFunctions = new DefaultFunctions(functionRegistry);
    assertSame(ValueType.BOOLEAN, functionRegistry.returnTypeOf("isHetu", ValueType.STRING));
    assertSame(ValueType.BOOLEAN, functionRegistry.returnTypeOf("isNotHetu", ValueType.STRING));
    assertSame(ValueType.BOOLEAN, functionRegistry.returnTypeOf("isLyt", ValueType.STRING));
    assertSame(ValueType.BOOLEAN, functionRegistry.returnTypeOf("isNotLyt", ValueType.STRING));
    assertSame(ValueType.INTEGER, functionRegistry.returnTypeOf("lengthOf", ValueType.STRING));
    assertSame(ValueType.INTEGER, functionRegistry.returnTypeOf("count", ValueType.arrayOf(ValueType.INTEGER)));
    assertSame(ValueType.INTEGER, functionRegistry.returnTypeOf("count", ValueType.arrayOf(ValueType.STRING)));

    org.assertj.core.api.Assertions.assertThatThrownBy(() -> functionRegistry.returnTypeOf("lengthOf")).isInstanceOf(VariableNotDefinedException.class);
  }

}
