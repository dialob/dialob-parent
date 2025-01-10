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
package io.dialob.boot.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import static com.fasterxml.jackson.databind.DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS;

public abstract class BaseRestTest {

  @ImportAutoConfiguration({ErrorMvcAutoConfiguration.class})
  public static class TestConfiguration  {

    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter(MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter, Validator[] validators) {
      RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
      requestMappingHandlerAdapter.getMessageConverters().add(mappingJackson2HttpMessageConverter);
      requestMappingHandlerAdapter.setWebBindingInitializer(binder -> binder.addValidators(validators));
      return requestMappingHandlerAdapter;
    }

    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper()
        .disable(READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .disable(WRITE_DATES_AS_TIMESTAMPS,
          WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        .registerModule(new JavaTimeModule());
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
      MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
      return converter;
    }
  }

  @Inject
  protected WebApplicationContext wac;

  @Inject
  protected ObjectMapper objectMapper;

  protected MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
    this.mockMvc = builder.build();
  }


  protected static ResultMatcher OK = MockMvcResultMatchers.status().isOk();

}
