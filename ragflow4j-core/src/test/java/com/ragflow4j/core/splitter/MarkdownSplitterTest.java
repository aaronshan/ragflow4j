package com.ragflow4j.core.splitter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownSplitterTest {
    private MarkdownSplitter splitter;
    private SplitterConfig config;

    @BeforeEach
    void setUp() {
        Map<String, String> options = new HashMap<>();
        config = new SplitterConfig(100, 10, 5, options);
        splitter = new MarkdownSplitter(config);
    }

    @Test
    void testEmptyInput() {
        assertTrue(splitter.split("").isEmpty(), "Empty input should return empty list");
        assertTrue(splitter.split(null).isEmpty(), "Null input should return empty list");
        assertTrue(splitter.split("   ").isEmpty(), "Whitespace input should return empty list");
    }

    @Test
    void testSimpleMarkdown() {
        String input = "This is a simple markdown text.";
        List<String> result = splitter.split(input);
        
        assertEquals(1, result.size(), "Should split into one chunk");
        assertEquals(input.trim(), result.get(0), "Content should match input");
    }

    @Test
    void testMarkdownWithHeaders() {
        String input = "# Header 1\n\nFirst paragraph.\n\n## Header 2\n\nSecond paragraph.";
        List<String> result = splitter.split(input);
        
        assertEquals(4, result.size(), "Should split into four chunks");
        assertEquals("# Header 1", result.get(0), "First chunk should be header 1");
        assertEquals("First paragraph.", result.get(1), "Second chunk should be first paragraph");
        assertEquals("## Header 2", result.get(2), "Third chunk should be header 2");
        assertEquals("Second paragraph.", result.get(3), "Fourth chunk should be second paragraph");
    }

    @Test
    void testMarkdownWithList() {
        String input = "# Shopping List\n\n- Item 1\n- Item 2\n- Item 3\n\nNotes below.";
        List<String> result = splitter.split(input);
        
        assertEquals(3, result.size(), "Should split into three chunks");
        assertEquals("# Shopping List", result.get(0), "First chunk should be header");
        assertEquals("- Item 1\n- Item 2\n- Item 3", result.get(1), "Second chunk should be list");
        assertEquals("Notes below.", result.get(2), "Third chunk should be notes");
    }

    @Test
    void testMarkdownWithCodeBlock() {
        String input = "Here's some code:\n\n```java\npublic class Test {\n    public static void main(String[] args) {\n        System.out.println(\"Hello\");\n    }\n}\n```\n\nEnd of code.";
        List<String> result = splitter.split(input);
        
        assertEquals(3, result.size(), "Should split into three chunks");
        assertEquals("Here's some code:", result.get(0), "First chunk should be introduction");
        assertTrue(result.get(1).startsWith("```java"), "Second chunk should be code block");
        assertTrue(result.get(1).endsWith("```"), "Code block should end with closing backticks");
        assertEquals("End of code.", result.get(2), "Third chunk should be conclusion");
    }

    @Test
    void testContentTypeSupport() {
        assertTrue(splitter.supports("text/markdown"), "Should support markdown content type");
        assertFalse(splitter.supports("text/plain"), "Should not support plain text content type");
    }

    @Test
    void testGetConfig() {
        assertSame(config, splitter.getConfig(), "Should return the same config instance");
    }
}