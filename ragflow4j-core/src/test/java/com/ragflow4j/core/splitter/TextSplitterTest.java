package com.ragflow4j.core.splitter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TextSplitterTest {
    private TextSplitter splitter;
    private SplitterConfig config;

    @BeforeEach
    void setUp() {
        Map<String, String> options = new HashMap<>();
        options.put("paragraphMarker", "\n\n");
        options.put("sentenceEndMarkers", ".!?");
        config = new SplitterConfig(100, 10, 5, options);
        splitter = new TextSplitter(config);
    }

    @Test
    void testEmptyInput() {
        assertTrue(splitter.split("").isEmpty(), "Empty input should return empty list");
        assertTrue(splitter.split(null).isEmpty(), "Null input should return empty list");
        assertTrue(splitter.split("   ").isEmpty(), "Whitespace input should return empty list");
    }

    @Test
    void testSimpleText() {
        String input = "This is a simple test.";
        List<String> result = splitter.split(input);
        
        assertEquals(1, result.size(), "Should split into one chunk");
        assertEquals(input, result.get(0), "Content should match input");
    }

    @Test
    void testParagraphSplitting() {
        String input = "First paragraph.\n\nSecond paragraph.\n\nThird paragraph.";
        List<String> result = splitter.split(input);
        
        assertEquals(3, result.size(), "Should split into three paragraphs");
        assertEquals("First paragraph.", result.get(0), "First paragraph should match");
        assertEquals("Second paragraph.", result.get(1), "Second paragraph should match");
        assertEquals("Third paragraph.", result.get(2), "Third paragraph should match");
    }

    @Test
    void testSentenceSplitting() {
        String input = "First sentence! Second sentence? Third sentence.";
        List<String> result = splitter.split(input);
        
        assertEquals(3, result.size(), "Should split into three sentences");
        assertEquals("First sentence!", result.get(0), "First sentence should match");
        assertEquals("Second sentence?", result.get(1), "Second sentence should match");
        assertEquals("Third sentence.", result.get(2), "Third sentence should match");
    }

    @Test
    void testLengthConstraints() {
        // Create a splitter with smaller chunk size
        Map<String, String> options = new HashMap<>();
        options.put("paragraphMarker", "\n\n");
        options.put("sentenceEndMarkers", ".!?");
        SplitterConfig smallConfig = new SplitterConfig(20, 5, 0, options);
        TextSplitter smallSplitter = new TextSplitter(smallConfig);

        String input = "This is a longer text that should be split into multiple chunks based on size.";
        List<String> result = smallSplitter.split(input);

        assertTrue(result.size() > 1, "Should split into multiple chunks");
        for (String chunk : result) {
            assertTrue(chunk.length() <= 20, "Each chunk should not exceed max size");
            assertTrue(chunk.length() >= 5, "Each chunk should meet minimum size");
        }
    }

    @Test
    void testSpecialPunctuation() {
        String input = "He said \"Hello!\" and left. She replied \"Goodbye?\" softly.";
        List<String> result = splitter.split(input);
        
        assertEquals(2, result.size(), "Should split into two sentences");
        assertEquals("He said \"Hello!\" and left.", result.get(0), "First sentence should include quotes");
        assertEquals("She replied \"Goodbye?\" softly.", result.get(1), "Second sentence should include quotes");
    }
}