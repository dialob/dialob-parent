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
package io.dialob.integration.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JsonMessageConverterTest {

    @Mock
    private ObjectMapper objectMapper;

    private JsonMessageConverter<TestObject> converter;

    static class TestObject {
        private String value;

        public TestObject() {}
        public TestObject(String value) { this.value = value; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        converter = new JsonMessageConverter<>(objectMapper, TestObject.class);
    }

    @Test
    void fromMessage_Success() throws JacksonException {
        TestObject testObject = new TestObject("test");
        Message<TestObject> message = new GenericMessage<>(testObject);
        when(objectMapper.writeValueAsString(testObject)).thenReturn("{\"value\":\"test\"}");

        Object result = converter.fromMessage(message, TestObject.class);

        assertEquals("{\"value\":\"test\"}", result);
    }

    @Test
    void fromMessage_JsonProcessingException() throws JacksonException {
        TestObject testObject = new TestObject("test");
        Message<TestObject> message = new GenericMessage<>(testObject);
        when(objectMapper.writeValueAsString(testObject)).thenThrow(new tools.jackson.core.JacksonException(""){});

        Object result = converter.fromMessage(message, TestObject.class);

        assertNull(result);
    }

    @Test
    void toMessage_Success() throws JacksonException {
        String payload = "{\"value\":\"test\"}";
        TestObject testObject = new TestObject("test");
        when(objectMapper.readValue(payload, TestObject.class)).thenReturn(testObject);

        Message<?> result = converter.toMessage(payload, null);

        assertNotNull(result);
        assertTrue(result.getPayload() instanceof TestObject);
        assertEquals("test", ((TestObject) result.getPayload()).getValue());
    }

    @Test
    void toMessage_JsonProcessingException() throws JacksonException {
        String payload = "invalid-json";
        when(objectMapper.readValue(payload, TestObject.class)).thenThrow(new tools.jackson.core.JacksonException(""){});

        Message<?> result = converter.toMessage(payload, null);

        assertNull(result);
    }
}
