package com.ragflow4j.core.loader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PdfDocumentLoaderTest {

    private PdfDocumentLoader loader;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loader = new PdfDocumentLoader();
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.pdf", "test.PDF", "test.Pdf"})
    void testSupportsValidExtensions(String filename) {
        assertTrue(loader.supports(Paths.get(filename)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test.txt", "test.doc", "test", "test."})
    void testSupportsInvalidExtensions(String filename) {
        assertFalse(loader.supports(Paths.get(filename)));
    }

    @Test
    void testLoadValidPdfFile() throws IOException {
        Path testFile = tempDir.resolve("test.pdf");
        createTestPdf(testFile);

        String content = loader.load(testFile);
        assertTrue(content.contains("Test PDF Content"));
        assertTrue(content.contains("Test Author"));
        assertTrue(content.contains("Test Title"));
    }

    @Test
    void testLoadMultiPagePdf() throws IOException {
        Path testFile = tempDir.resolve("multipage.pdf");
        createMultiPagePdf(testFile);

        String content = loader.load(testFile);
        assertTrue(content.contains("Page 1"));
        assertTrue(content.contains("Page 2"));
        assertTrue(content.contains("Page 3"));
    }

    @Test
    void testLoadPdfWithoutMetadata() throws IOException {
        Path testFile = tempDir.resolve("nometadata.pdf");
        createPdfWithoutMetadata(testFile);

        String content = loader.load(testFile);
        assertTrue(content.contains("Test Content"));
        assertFalse(content.contains("Author"));
    }

    @Test
    void testLoadEmptyPdf() throws IOException {
        Path testFile = tempDir.resolve("empty.pdf");
        createEmptyPdf(testFile);

        String content = loader.load(testFile);
        assertTrue(content.contains("Document Content"));
        assertTrue(content.trim().endsWith("---"));
    }

    @Test
    void testLoadAsync() throws ExecutionException, InterruptedException, IOException {
        Path testFile = tempDir.resolve("test.pdf");
        createTestPdf(testFile);

        String content = loader.loadAsync(testFile).get();
        assertTrue(content.contains("Test PDF Content"));
    }

    @Test
    void testLoadAsyncWithTimeout() throws IOException {
        Path testFile = tempDir.resolve("test.pdf");
        createTestPdf(testFile);

        assertTimeoutPreemptively(
            java.time.Duration.ofSeconds(5),
            () -> {
                String content = loader.loadAsync(testFile).get(3, TimeUnit.SECONDS);
                assertTrue(content.contains("Test PDF Content"));
            }
        );
    }

    @Test
    void testLoadNonexistentFile() {
        Path nonexistentFile = tempDir.resolve("nonexistent.pdf");
        assertThrows(IOException.class, () -> loader.load(nonexistentFile));
    }

    @Test
    void testLoadDirectory() {
        assertThrows(IOException.class, () -> loader.load(tempDir));
    }

    @Test
    void testLoadFileWithoutReadPermission() throws IOException {
        Path testFile = tempDir.resolve("noperm.pdf");
        createTestPdf(testFile);
        testFile.toFile().setReadable(false);

        assertThrows(IOException.class, () -> loader.load(testFile));
    }

    private void createTestPdf(Path path) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Test PDF Content");
                contentStream.endText();
            }

            PDDocumentInformation info = new PDDocumentInformation();
            info.setAuthor("Test Author");
            info.setTitle("Test Title");
            info.setSubject("Test Subject");
            info.setCreationDate(Calendar.getInstance());
            document.setDocumentInformation(info);

            document.save(path.toFile());
        }
    }

    private void createMultiPagePdf(Path path) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (int i = 1; i <= 3; i++) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("Page " + i);
                    contentStream.endText();
                }
            }
            document.save(path.toFile());
        }
    }

    private void createPdfWithoutMetadata(Path path) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Test Content");
                contentStream.endText();
            }

            document.save(path.toFile());
        }
    }

    private void createEmptyPdf(Path path) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            document.save(path.toFile());
        }
    }
} 