package com.ragflow4j.core.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class DocumentLoaderFactoryTest {

    @BeforeEach
    void setUp() {
        DocumentLoaderFactory.clearLoaders();
    }

    @Test
    void testRegisterLoader() {
        DocumentLoader loader = new TextDocumentLoader();
        DocumentLoaderFactory.registerLoader(loader);
        assertEquals(1, DocumentLoaderFactory.getLoaderCount());
    }

    @Test
    void testRegisterDuplicateLoader() {
        DocumentLoader loader = new TextDocumentLoader();
        DocumentLoaderFactory.registerLoader(loader);
        DocumentLoaderFactory.registerLoader(loader);
        assertEquals(2, DocumentLoaderFactory.getLoaderCount());
    }

    @Test
    void testRegisterMultipleLoaders() {
        List<DocumentLoader> loaders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            loaders.add(new TextDocumentLoader());
        }
        loaders.forEach(DocumentLoaderFactory::registerLoader);
        assertEquals(5, DocumentLoaderFactory.getLoaderCount());
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.txt", "test.text", "test.log"})
    void testGetLoaderForTextFiles(String filename) {
        DocumentLoaderFactory.registerLoader(new TextDocumentLoader());
        DocumentLoaderFactory.registerLoader(new PdfDocumentLoader());

        Optional<DocumentLoader> loader = DocumentLoaderFactory.getLoader(Paths.get(filename));
        assertTrue(loader.isPresent());
        assertTrue(loader.get() instanceof TextDocumentLoader);
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.pdf", "test.PDF", "test.Pdf"})
    void testGetLoaderForPdfFiles(String filename) {
        DocumentLoaderFactory.registerLoader(new TextDocumentLoader());
        DocumentLoaderFactory.registerLoader(new PdfDocumentLoader());

        Optional<DocumentLoader> loader = DocumentLoaderFactory.getLoader(Paths.get(filename));
        assertTrue(loader.isPresent());
        assertTrue(loader.get() instanceof PdfDocumentLoader);
    }

    @Test
    void testGetLoaderForUnsupportedFile() {
        DocumentLoaderFactory.registerLoader(new TextDocumentLoader());
        DocumentLoaderFactory.registerLoader(new PdfDocumentLoader());

        Optional<DocumentLoader> loader = DocumentLoaderFactory.getLoader(Paths.get("test.doc"));
        assertFalse(loader.isPresent());
    }

    @Test
    void testClearLoaders() {
        DocumentLoaderFactory.registerLoader(new TextDocumentLoader());
        DocumentLoaderFactory.registerLoader(new PdfDocumentLoader());
        assertEquals(2, DocumentLoaderFactory.getLoaderCount());

        DocumentLoaderFactory.clearLoaders();
        assertEquals(0, DocumentLoaderFactory.getLoaderCount());
    }

    @Test
    void testConcurrentLoaderRegistration() throws InterruptedException {
        int numThreads = 10;
        int loadersPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        IntStream.range(0, numThreads).forEach(i -> 
            executor.submit(() -> {
                try {
                    for (int j = 0; j < loadersPerThread; j++) {
                        DocumentLoaderFactory.registerLoader(new TextDocumentLoader());
                    }
                } finally {
                    latch.countDown();
                }
            })
        );

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        
        assertEquals(numThreads * loadersPerThread, DocumentLoaderFactory.getLoaderCount());
    }

    @Test
    void testDefaultLoaders() {
        // Clear any existing loaders
        DocumentLoaderFactory.clearLoaders();
        
        // Re-register default loaders
        DocumentLoaderFactory.registerLoader(new TextDocumentLoader());
        DocumentLoaderFactory.registerLoader(new PdfDocumentLoader());

        // Test both default loaders are working
        assertTrue(DocumentLoaderFactory.getLoader(Paths.get("test.txt")).isPresent());
        assertTrue(DocumentLoaderFactory.getLoader(Paths.get("test.pdf")).isPresent());
        assertFalse(DocumentLoaderFactory.getLoader(Paths.get("test.doc")).isPresent());
    }

    @Test
    void testGetLoaderWithNullPath() {
        DocumentLoaderFactory.registerLoader(new TextDocumentLoader());
        assertThrows(NullPointerException.class, () -> DocumentLoaderFactory.getLoader(null));
    }

    @Test
    void testRegisterNullLoader() {
        assertThrows(NullPointerException.class, () -> DocumentLoaderFactory.registerLoader(null));
    }
} 