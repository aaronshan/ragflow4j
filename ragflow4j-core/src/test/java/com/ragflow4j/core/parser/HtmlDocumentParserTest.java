package com.ragflow4j.core.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HtmlDocumentParserTest {
    
    private HtmlDocumentParser parser;
    private ParserConfig config;
    
    @BeforeEach
    void setUp() {
        Map<String, String> options = new HashMap<>();
        options.put("encoding", "UTF-8");
        config = new ParserConfig(4096, true, options);
        parser = new HtmlDocumentParser(config);
    }
    
    @Test
    void testSupportsContentType() {
        assertTrue(parser.supports("text/html"));
        assertFalse(parser.supports("text/plain"));
        assertFalse(parser.supports("application/pdf"));
    }
    
    @Test
    void testParseSimpleHtml() {
        String content = "<!DOCTYPE html><html><head><title>Test Document</title></head>" +
            "<body><h1>Main Title</h1><p>First paragraph</p>" +
            "<h2>Section 1</h2><p>Section 1 content</p>" +
            "<h2>Section 2</h2><p>Section 2 content</p></body></html>";
            
        ParseResult result = parser.parse(content);
        
        assertEquals("Test Document", result.getTitle());
        assertTrue(result.getSections().contains("First paragraph"));
        assertTrue(result.getSections().contains("Section 1 content"));
        assertTrue(result.getSections().contains("Section 2 content"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sections = (List<Map<String, Object>>) result.getStructure().get("sections");
        assertEquals(3, sections.size());
        assertEquals("Main Title", sections.get(0).get("title"));
        assertEquals(1, sections.get(0).get("level"));
        assertEquals("Section 1", sections.get(1).get("title"));
        assertEquals(2, sections.get(1).get("level"));
    }
    
    @Test
    void testParseWithMetadata() {
        String content = "<!DOCTYPE html><html><head>" +
            "<title>Test Document</title>" +
            "<meta name=\"author\" content=\"John Doe\">" +
            "<meta name=\"description\" content=\"Test description\">" +
            "</head><body><p>Content</p></body></html>";
            
        ParseResult result = parser.parse(content);
        
        assertEquals("Test Document", result.getTitle());
        assertEquals("John Doe", result.getMetadata().get("author"));
        assertEquals("Test description", result.getMetadata().get("description"));
    }
    
    @Test
    void testParseEmptyHtml() {
        String content = "<html><body></body></html>";
        ParseResult result = parser.parse(content);
        
        assertEquals("Untitled Document", result.getTitle());
        assertTrue(result.getSections().isEmpty());
    }
    
    @Test
    void testParseComplexStructure() {
        String content = "<!DOCTYPE html><html><head><title>Complex Document</title></head>" +
            "<body>" +
            "<article><h1>Article Title</h1><p>Article content</p></article>" +
            "<section><h2>Section Title</h2><div>Section content</div></section>" +
            "<div class=\"content\"><p>Additional content</p></div>" +
            "</body></html>";
            
        ParseResult result = parser.parse(content);
        
        assertEquals("Complex Document", result.getTitle());
        assertTrue(result.getSections().contains("Article content"));
        assertTrue(result.getSections().contains("Section content"));
        assertTrue(result.getSections().contains("Additional content"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sections = (List<Map<String, Object>>) result.getStructure().get("sections");
        assertTrue(sections.size() >= 2);
        assertEquals("Article Title", sections.get(0).get("title"));
        assertEquals("Section Title", sections.get(1).get("title"));
    }
    
    @Test
    void testContentTypeDetection() {
        assertTrue(parser.supports(parser.detectContentType("<!DOCTYPE html><html></html>")));
        assertTrue(parser.supports(parser.detectContentType("<html><body>Test</body></html>")));
        assertFalse(parser.supports(parser.detectContentType("Just plain text")));
        assertFalse(parser.supports(parser.detectContentType("")));
        assertFalse(parser.supports(parser.detectContentType(null)));
    }
}
 