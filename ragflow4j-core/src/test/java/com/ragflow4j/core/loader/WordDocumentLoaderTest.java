package com.ragflow4j.core.loader;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class WordDocumentLoaderTest {
    private WordDocumentLoader loader;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loader = new WordDocumentLoader();
    }

    @Test
    void testSupportsDocx() {
        assertTrue(loader.supports(Paths.get("test.docx")));
        assertFalse(loader.supports(Paths.get("test.txt")));
    }

    @Test
    void testLoadDocx() throws Exception {
        Path docxPath = tempDir.resolve("test.docx");
        
        // 创建测试DOCX文件
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText("Test content for DOCX");
            
            // 设置文档属性
            document.getProperties().getCoreProperties().setTitle("Test Title");
            document.getProperties().getCoreProperties().setCreator("Test Author");
            
            try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
                document.write(out);
            }
        }

        String content = loader.load(docxPath);
        assertTrue(content.contains("Test content for DOCX"));
        assertTrue(content.contains("Title: Test Title"));
        assertTrue(content.contains("Author: Test Author"));
    }

    @Test
    void testAsyncLoad() throws Exception {
        Path docxPath = tempDir.resolve("test.docx");
        
        // 创建测试DOCX文件
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText("Test content for async loading");
            
            try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
                document.write(out);
            }
        }

        CompletableFuture<String> future = loader.loadAsync(docxPath);
        String content = future.get(5, TimeUnit.SECONDS);
        assertTrue(content.contains("Test content for async loading"));
    }

    @Test
    void testLoadInvalidFile() {
        Path invalidPath = tempDir.resolve("invalid.docx");
        assertThrows(IOException.class, () -> loader.load(invalidPath));
    }
} 