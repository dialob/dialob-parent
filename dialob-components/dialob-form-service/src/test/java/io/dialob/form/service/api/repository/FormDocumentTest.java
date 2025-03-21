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
package io.dialob.form.service.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dialob.api.form.Form;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class FormDocumentTest {

    @Test
    void test() throws Exception {
        String s = "{\"_id\": \"d34d5d75297bd4ec977c8beb3003c827\",\"_rev\": \"6-5b916f027a4901003f3a64d05331429d\",\"updated\": null,"
                + "\"saving\": false,\"failed\": null,"
                + "\"data\": {\"group\": {\"type\": \"group\",\"items\": [\"text\",\"text1\","
                + "\"text2\"],\"className\": [],\"id\": \"group\",\"label\": { \"en\": \"Group\"}},\"text\": {\"id\": \"text\",\"type\": \"text\","
                + "\"label\": {\"en\": \"Question \"}},\"text1\": {\"id\": \"text1\",\"type\": \"text\",\"label\": {\"en\": \"Question 1\"}},\"text2\": {"
                + "\"id\": \"text2\",\"type\": \"text\",\"label\": {\"en\": \"Question 2\"}},\"group1\": {\"type\": \"group\",\"items\": [],"
                + "\"className\": [],\"id\": \"group1\",\"label\": {\"en\":\"Group 1\"}},\"group2\": {\"type\": \"group\",\"items\": [],\"className\": ["
                + "],\"id\": \"group2\",\"label\": {\"en\": \"Group 2\"}}}, \"metadata\": {\"lastSaved\": null,\"created\": \"2015-10-09T13:34:01.622Z\",\"label\":\"test\"}}";
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        Form formDocument = mapper.readValue(s, Form.class);
        assertEquals(6, formDocument.getData().size());
        assertEquals(Date.from(Instant.parse("2015-10-09T13:34:01.622Z")), formDocument.getMetadata().getCreated());
        assertNotNull(formDocument);
    }


}
