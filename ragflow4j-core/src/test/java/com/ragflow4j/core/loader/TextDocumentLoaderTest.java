package com.ragflow4j.core.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.text.Normalizer;

import static org.junit.jupiter.api.Assertions.*;

class TextDocumentLoaderTest {

    private TextDocumentLoader loader;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loader = new TextDocumentLoader();
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.txt", "test.text", "test.log"})
    void testSupportsValidExtensions(String filename) {
        assertTrue(loader.supports(Paths.get(filename)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.pdf", "test.doc", "test", "test."})
    void testSupportsInvalidExtensions(String filename) {
        assertFalse(loader.supports(Paths.get(filename)));
    }

    @Test
    void testLoadValidTextFile() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        String content = "Test content\nLine 2\n你好，世界！";
        Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));

        String loadedContent = loader.load(testFile);
        assertEquals(content, loadedContent);
    }

    @Test
    void testLoadLargeTextFile() throws IOException {
        Path testFile = tempDir.resolve("large.txt");
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            content.append("Line ").append(i).append("\n");
        }
        Files.write(testFile, content.toString().getBytes(StandardCharsets.UTF_8));

        String loadedContent = loader.load(testFile);
        assertEquals(content.toString(), loadedContent);
    }

    @Test
    void testLoadEmptyFile() throws IOException {
        Path testFile = tempDir.resolve("empty.txt");
        Files.createFile(testFile);

        String loadedContent = loader.load(testFile);
        assertEquals("", loadedContent);
    }

    @Test
    void testLoadWithDifferentEncodings() throws IOException {
        String content = "Test content with special characters: é è à ñ";
        Path testFile = tempDir.resolve("encoded.txt");
        
        // Test with different encodings
        Charset[] encodings = {StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1, StandardCharsets.UTF_16BE};
        for (Charset encoding : encodings) {
            // For UTF-16BE, prepend BOM
            if (encoding == StandardCharsets.UTF_16BE) {
                byte[] bom = new byte[] {(byte)0xFE, (byte)0xFF};
                byte[] contentBytes = content.getBytes(encoding);
                byte[] bytesWithBom = new byte[bom.length + contentBytes.length];
                System.arraycopy(bom, 0, bytesWithBom, 0, bom.length);
                System.arraycopy(contentBytes, 0, bytesWithBom, bom.length, contentBytes.length);
                Files.write(testFile, bytesWithBom);
            } else {
                Files.write(testFile, content.getBytes(encoding));
            }
            
            String loadedContent = loader.load(testFile);
            
            // Normalize both strings to NFC form before comparison
            String normalizedExpected = Normalizer.normalize(content, Normalizer.Form.NFC);
            String normalizedActual = Normalizer.normalize(loadedContent, Normalizer.Form.NFC);
            assertEquals(normalizedExpected, normalizedActual, 
                       "Failed with encoding: " + encoding.name());
        }
    }

    @Test
    void testLoadAsync() throws ExecutionException, InterruptedException, IOException {
        Path testFile = tempDir.resolve("test.txt");
        String content = "Test content\nLine 2";
        Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));

        String loadedContent = loader.loadAsync(testFile).get();
        assertEquals(content, loadedContent);
    }

    @Test
    void testLoadAsyncWithTimeout() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        String content = "Test content\nLine 2";
        Files.write(testFile, content.getBytes(StandardCharsets.UTF_8));

        assertTimeoutPreemptively(
            java.time.Duration.ofSeconds(5),
            () -> {
                String loadedContent = loader.loadAsync(testFile).get(3, TimeUnit.SECONDS);
                assertEquals(content, loadedContent);
            }
        );
    }

    @Test
    void testLoadNonexistentFile() {
        Path nonexistentFile = tempDir.resolve("nonexistent.txt");
        assertThrows(IOException.class, () -> loader.load(nonexistentFile));
    }

    @Test
    void testLoadDirectory() {
        assertThrows(IOException.class, () -> loader.load(tempDir));
    }

    @Test
    void testLoadFileWithoutReadPermission() throws IOException {
        Path testFile = tempDir.resolve("noperm.txt");
        Files.write(testFile, "test".getBytes());
        testFile.toFile().setReadable(false);

        assertThrows(IOException.class, () -> loader.load(testFile));
    }
} 