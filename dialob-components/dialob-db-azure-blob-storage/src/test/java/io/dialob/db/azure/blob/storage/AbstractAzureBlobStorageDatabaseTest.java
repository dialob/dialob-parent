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
package io.dialob.db.azure.blob.storage;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.*;
import io.dialob.db.spi.exceptions.DocumentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AbstractAzureBlobStorageDatabaseTest {

    @Mock
    private BlobContainerClient blobContainerClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private BlobClient blobClient;

    private TestDatabase database;

    static class TestDocument {
        @Id
        private String id;
        @Version
        private String rev;

        public TestDocument() {}

        public TestDocument(String id, String rev) {
            this.id = id;
            this.rev = rev;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getRev() { return rev; }
        public void setRev(String rev) { this.rev = rev; }
    }

    static class TestDatabase extends AbstractAzureBlobStorageDatabase<TestDocument> {
        public TestDatabase(BlobContainerClient blobContainerClient, ObjectMapper objectMapper) {
            super(blobContainerClient, TestDocument.class, objectMapper, "test-prefix", ".json");
        }

        @Override
        protected String id(TestDocument document) {
            return document.getId();
        }

        @Override
        protected String rev(TestDocument document) {
            return document.getRev();
        }

        @Override
        protected TestDocument updateDocumentId(TestDocument document, String id) {
            document.setId(id);
            return document;
        }

        @Override
        protected TestDocument updateDocumentRev(TestDocument document, String rev) {
            document.setRev(rev);
            return document;
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        database = new TestDatabase(blobContainerClient, objectMapper);
    }

    @Test
    void testTenantPrefix() {
        assertEquals("test-prefix/tenant1", database.tenantPrefix("tenant1"));
    }

    @Test
    void testObjectName() {
        assertEquals("test-prefix/tenant1/doc1.json", database.objectName("tenant1", "doc1"));
    }

    @Test
    void testExtractObjectName() {
        assertEquals("doc1.json", database.extractObjectName("test-prefix/tenant1/doc1.json"));
    }

    @Test
    void testFindOne_Success() throws Exception {
        BinaryData binaryData = mock(BinaryData.class);
        InputStream inputStream = new ByteArrayInputStream("{}".getBytes());
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);
        when(blobClient.downloadContent()).thenReturn(binaryData);
        when(binaryData.toStream()).thenReturn(inputStream);

        TestDocument expectedDoc = new TestDocument("doc1", "1");
        when(objectMapper.readValue(any(InputStream.class), eq(TestDocument.class))).thenReturn(expectedDoc);

        TestDocument result = database.findOne("tenant1", "doc1");

        assertNotNull(result);
        assertEquals("doc1", result.getId());
        verify(blobClient).downloadContent();
    }

    @Test
    void testFindOne_NotFound() {
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);

        BlobStorageException exception = mock(BlobStorageException.class);
        when(exception.getErrorCode()).thenReturn(BlobErrorCode.BLOB_NOT_FOUND);
        when(blobClient.downloadContent()).thenThrow(exception);

        assertThrows(DocumentNotFoundException.class, () -> database.findOne("tenant1", "doc1"));
    }

    @Test
    void testExists_True() {
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(true);

        assertTrue(database.exists("tenant1", "doc1"));
    }

    @Test
    void testExists_False() {
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(false);

        assertFalse(database.exists("tenant1", "doc1"));
    }

    @Test
    void testDelete_Success() {
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(true);

        assertTrue(database.delete("tenant1", "doc1"));
        verify(blobClient).delete();
    }

    @Test
    void testDelete_NotExists() {
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(false);

        assertFalse(database.delete("tenant1", "doc1"));
        verify(blobClient, never()).delete();
    }

    @Test
    void testSave_NewDocument() throws Exception {
        TestDocument doc = new TestDocument();
        when(blobContainerClient.getBlobClient(anyString())).thenReturn(blobClient);
        when(objectMapper.writeValueAsBytes(any(TestDocument.class))).thenReturn("{}".getBytes());

        TestDocument savedDoc = database.save("tenant1", doc);

        assertNotNull(savedDoc.getId());
        assertEquals("1", savedDoc.getRev());
        verify(blobClient).upload(any(BinaryData.class), eq(true));
        verify(blobClient).setHttpHeaders(any(BlobHttpHeaders.class));
    }

    @Test
    void testSave_UpdateDocument() throws Exception {
        TestDocument existingDoc = new TestDocument("doc1", "1");
        TestDocument docToSave = new TestDocument("doc1", "1");

        // Mock findOne for existing doc
        BinaryData binaryData = mock(BinaryData.class);
        InputStream inputStream = new ByteArrayInputStream("{}".getBytes());
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);
        when(blobClient.downloadContent()).thenReturn(binaryData);
        when(binaryData.toStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(InputStream.class), eq(TestDocument.class))).thenReturn(existingDoc);
        when(objectMapper.writeValueAsBytes(any(TestDocument.class))).thenReturn("{}".getBytes());

        TestDocument savedDoc = database.save("tenant1", docToSave);

        assertEquals("doc1", savedDoc.getId());
        assertEquals("2", savedDoc.getRev());
        verify(blobClient).upload(any(BinaryData.class), eq(true));
    }

    @Test
    void testSave_UpdateDocumentVersionConflict() throws Exception {
        TestDocument existingDoc = new TestDocument("doc1", "2"); // DB has rev 2
        TestDocument docToSave = new TestDocument("doc1", "1"); // We try to save rev 1

        BinaryData binaryData = mock(BinaryData.class);
        InputStream inputStream = new ByteArrayInputStream("{}".getBytes());
        when(blobContainerClient.getBlobClient("test-prefix/tenant1/doc1.json")).thenReturn(blobClient);
        when(blobClient.downloadContent()).thenReturn(binaryData);
        when(binaryData.toStream()).thenReturn(inputStream);
        when(objectMapper.readValue(any(InputStream.class), eq(TestDocument.class))).thenReturn(existingDoc);

        assertThrows(VersionConflictException.class, () -> database.save("tenant1", docToSave));
    }

    @Test
    void testLoadFile_Corrupted() throws Exception {
        JacksonException jacksonException = mock(JacksonException.class);
        when(objectMapper.readValue(any(InputStream.class), eq(TestDocument.class))).thenThrow(jacksonException);

        TestDocument result = database.loadFile("doc1", new ByteArrayInputStream("".getBytes()));
        assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testForAllObjects() {
        PagedIterable<BlobItem> pagedIterable = mock(PagedIterable.class);
        BlobItem blobItem = mock(BlobItem.class);

        when(blobContainerClient.listBlobs(any(ListBlobsOptions.class), any(Duration.class))).thenReturn(pagedIterable);
        doAnswer(invocation -> {
            java.util.function.Consumer<BlobItem> consumer = invocation.getArgument(0);
            consumer.accept(blobItem);
            return null;
        }).when(pagedIterable).forEach(any(java.util.function.Consumer.class));

        java.util.function.Consumer<BlobItem> consumer = mock(java.util.function.Consumer.class);
        database.forAllObjects("tenant1", consumer);

        verify(blobContainerClient).listBlobs(argThat(options ->
            options.getPrefix().equals("test-prefix/tenant1") &&
            options.getMaxResultsPerPage().equals(200)
        ), eq(Duration.ofSeconds(20)));
        verify(consumer).accept(blobItem);
    }
}
