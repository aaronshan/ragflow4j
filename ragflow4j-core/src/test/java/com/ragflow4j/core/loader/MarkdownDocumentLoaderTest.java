package com.ragflow4j.core.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownDocumentLoaderTest {
    private MarkdownDocumentLoader loader;
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        loader = new MarkdownDocumentLoader();
    }

    @Test
    void testSupportsMarkdown() {
        assertTrue(loader.supports(Paths.get("test.md")));
        assertTrue(loader.supports(Paths.get("test.markdown")));
        assertFalse(loader.supports(Paths.get("test.txt")));
    }

    @Test
    void testLoadMarkdownWithMetadata() throws Exception {
        String content = "---\n" +
                "title: Test Document\n" +
                "author: Test Author\n" +
                "date: 2025-03-04\n" +
                "---\n" +
                "# Heading 1\n" +
                "\n" +
                "This is a test paragraph.\n" +
                "\n" +
                "## Heading 2\n" +
                "\n" +
                "* List item 1\n" +
                "* List item 2\n" +
                "\n" +
                "```java\n" +
                "public class Test {\n" +
                "    // Test code\n" +
                "}\n" +
                "```\n";
        
        Path mdPath = tempDir.resolve("test.md");
        Files.write(mdPath, content.getBytes(StandardCharsets.UTF_8));

        String result = loader.load(mdPath);
        
        // 验证元数据
        assertTrue(result.contains("title: Test Document"));
        assertTrue(result.contains("author: Test Author"));
        
        // 验证文档结构
        assertTrue(result.contains("# Heading 1"));
        assertTrue(result.contains("## Heading 2"));
        assertTrue(result.contains("* List"));
        assertTrue(result.contains("```java"));
        
        // 验证内容
        assertTrue(result.contains("This is a test paragraph"));
        assertTrue(result.contains("List item 1"));
        assertTrue(result.contains("List item 2"));
    }

    @Test
    void testLoadMarkdownWithoutMetadata() throws Exception {
        String content = "# Simple Document\n" +
                "\n" +
                "Just a simple test.\n" +
                "\n" +
                "* Item 1\n" +
                "* Item 2\n";
        
        Path mdPath = tempDir.resolve("simple.md");
        Files.write(mdPath, content.getBytes(StandardCharsets.UTF_8));

        String result = loader.load(mdPath);
        
        // 验证没有元数据部分
        assertFalse(result.contains("---"));
        
        // 验证文档结构和内容
        assertTrue(result.contains("# Simple Document"));
        assertTrue(result.contains("Just a simple test"));
        assertTrue(result.contains("Item 1"));
        assertTrue(result.contains("Item 2"));
    }

    @Test
    void testAsyncLoad() throws Exception {
        String content = "# Async Test\n\nTesting async loading.";
        Path mdPath = tempDir.resolve("async.md");
        Files.write(mdPath, content.getBytes(StandardCharsets.UTF_8));

        CompletableFuture<String> future = loader.loadAsync(mdPath);
        String result = future.get(5, TimeUnit.SECONDS);
        
        assertTrue(result.contains("# Async Test"));
        assertTrue(result.contains("Testing async loading"));
    }

    @Test
    void testLoadInvalidFile() {
        Path invalidPath = tempDir.resolve("invalid.md");
        assertThrows(IOException.class, () -> loader.load(invalidPath));
    }

    @Test
    void testComplexMarkdown() throws Exception {
        String content = "# Main Title\n" +
                "\n" +
                "## Section 1\n" +
                "\n" +
                "1. First item\n" +
                "2. Second item\n" +
                "   * Sub item 1\n" +
                "   * Sub item 2\n" +
                "\n" +
                "## Section 2\n" +
                "\n" +
                "```python\n" +
                "def test():\n" +
                "    print(\"Hello\")\n" +
                "```\n" +
                "\n" +
                "> This is a blockquote\n" +
                "\n" +
                "| Header 1 | Header 2 |\n" +
                "|----------|----------|\n" +
                "| Cell 1   | Cell 2   |";
        
        Path mdPath = tempDir.resolve("complex.md");
        Files.write(mdPath, content.getBytes(StandardCharsets.UTF_8));

        String result = loader.load(mdPath);
        
        // 验证文档结构
        assertTrue(result.contains("# Main Title"));
        assertTrue(result.contains("## Section 1"));
        assertTrue(result.contains("## Section 2"));
        assertTrue(result.contains("1. Ordered List"));
        assertTrue(result.contains("```python"));
        
        // 验证内容
        assertTrue(result.contains("First item"));
        assertTrue(result.contains("Second item"));
        assertTrue(result.contains("Sub item"));
        assertTrue(result.contains("This is a blockquote"));
        assertTrue(result.contains("Header 1"));
        assertTrue(result.contains("Cell 1"));
    }
} 