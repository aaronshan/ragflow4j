package com.ragflow4j.core.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TextDocumentParserTest {
    
    private TextDocumentParser parser;
    private ParserConfig config;
    
    @BeforeEach
    void setUp() {
        Map<String, String> options = new HashMap<>();
        options.put("encoding", "UTF-8");
        config = new ParserConfig(4096, true, options);
        parser = new TextDocumentParser(config);
    }
    
    @Test
    void testSupportsContentType() {
        assertTrue(parser.supports("text/plain"));
        assertFalse(parser.supports("text/html"));
        assertFalse(parser.supports("application/pdf"));
    }
    
    @Test
    void testParseSimpleText() {
        String content = "Title: Test Document\n\n" +
            "This is a test paragraph.\n\n" +
            "# Section 1\n\n" +
            "Content of section 1.\n\n" +
            "## Subsection 1.1\n\n" +
            "Content of subsection 1.1.";
            
        ParseResult result = parser.parse(content);
        
        assertEquals("Test Document", result.getTitle());
        assertEquals(4, result.getSections().size());
        assertTrue(result.getMetadata().containsKey("charCount"));
        assertTrue(result.getMetadata().containsKey("wordCount"));
        assertTrue(result.getMetadata().containsKey("lineCount"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sections = (List<Map<String, Object>>) result.getStructure().get("sections");
        assertEquals(2, sections.size());
        assertEquals("Section 1", sections.get(0).get("title"));
        assertEquals("Subsection 1.1", sections.get(1).get("title"));
    }
    
    @Test
    void testParseEmptyContent() {
        String content = "";
        ParseResult result = parser.parse(content);
        
        assertEquals("Untitled Document", result.getTitle());
        assertTrue(result.getSections().isEmpty());
        assertEquals("0", result.getMetadata().get("wordCount"));
    }
    
    @Test
    void testParseWithMetadata() {
        String content = "Title: Test Document\n" +
            "Author: John Doe\n" +
            "Date: 2025-03-05\n\n" +
            "Content goes here.";
            
        ParseResult result = parser.parse(content);
        
        assertEquals("Test Document", result.getTitle());
        assertTrue(result.getSections().contains("Content goes here."));
    }
    
    @Test
    void testContentTypeDetection() {
        String htmlContent = "<!DOCTYPE html><html><body>Test</body></html>";
        String xmlContent = "<?xml version=\"1.0\"?><root>Test</root>";
        String textContent = "Just plain text";
        
        assertEquals("text/html", parser.detectContentType(htmlContent));
        assertEquals("text/xml", parser.detectContentType(xmlContent));
        assertEquals("text/plain", parser.detectContentType(textContent));
        assertEquals("text/plain", parser.detectContentType(""));
        assertEquals("text/plain", parser.detectContentType(null));
    }
} 