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
package io.dialob.spring.boot;

import io.dialob.cache.DialobCacheAutoConfiguration;
import io.dialob.session.engine.DialobProgramService;
import io.dialob.session.engine.program.DialobProgram;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class DialobSpringBootAutoConfigurationTest {

  @Configuration(proxyBeanMethods = false)
  @EnableCaching
  public static class MockConfigurations {
    @Bean
    public DialobProgramService dialobProgramService() {
      DialobProgramService mock = Mockito.mock(DialobProgramService.class);
      when(mock.findByFormId("123")).thenReturn(Mockito.mock(DialobProgram.class));
      when(mock.findByFormId("321")).thenReturn(Mockito.mock(DialobProgram.class));
      return mock;
    }
  }



  @Test
  void testDialobSpringBootAutoConfiguration() {
    new ApplicationContextRunner()
      .withUserConfiguration(DialobSpringBootAutoConfigurationTest.MockConfigurations.class)
      .withConfiguration(AutoConfigurations.of(
        DialobCacheAutoConfiguration.class,
        DialobSpringBootAutoConfiguration.class))
      .run(context -> {
        CacheManager cacheManager = context.getBean("cacheManager", CacheManager.class);
        Cache dialobProgramsCache = cacheManager.getCache("dialobProgramsCache");
        Assertions.assertNotNull(dialobProgramsCache);
        DialobProgramService service = context.getBean(DialobProgramService.class);

        service.findByFormId("123");
        service.findByFormId("123");
        service.findByFormId("123");
        service.findByFormId("321");
        service.findByFormIdAndRev("321",null);

        DialobProgramService singletonTarget = (DialobProgramService) AopProxyUtils.getSingletonTarget(service);
        Mockito.verify(singletonTarget, times(1)).findByFormId("123");
        Mockito.verify(singletonTarget, times(1)).findByFormId("321");
        Mockito.verifyNoMoreInteractions(singletonTarget);
      });
  }

}
