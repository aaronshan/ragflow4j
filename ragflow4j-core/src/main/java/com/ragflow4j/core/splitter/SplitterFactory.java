package com.ragflow4j.core.splitter;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating and managing document splitters.
 */
public class SplitterFactory {
    private static final Map<String, DocumentSplitter> splitters = new HashMap<>();
    private static final SplitterConfig defaultConfig;
    
    static {
        // Initialize default configuration
        Map<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("encoding", "UTF-8");
        defaultOptions.put("sentenceEndMarkers", ".!?");
        defaultOptions.put("paragraphMarker", "\n\n");
        
        defaultConfig = new SplitterConfig(
            1000,  // maxChunkSize
            100,   // minChunkSize
            50,    // overlap
            defaultOptions
        );
        
        // Register default splitters
        registerSplitter("text/plain", new TextSplitter(defaultConfig));
        registerSplitter("text/markdown", new MarkdownSplitter(defaultConfig));
    }
    
    /**
     * Register a new splitter for a content type
     *
     * @param contentType The content type
     * @param splitter The splitter implementation
     */
    public static void registerSplitter(String contentType, DocumentSplitter splitter) {
        splitters.put(contentType, splitter);
    }
    
    /**
     * Get a splitter for the given content type
     *
     * @param contentType The content type
     * @return The appropriate splitter
     * @throws UnsupportedOperationException if no splitter is found
     */
    public static DocumentSplitter getSplitter(String contentType) {
        DocumentSplitter splitter = splitters.get(contentType);
        if (splitter == null) {
            throw new UnsupportedOperationException("No splitter found for content type: " + contentType);
        }
        return splitter;
    }
    
    /**
     * Get the default splitter configuration
     *
     * @return The default configuration
     */
    public static SplitterConfig getDefaultConfig() {
        return defaultConfig;
    }
    
    /**
     * Clear all registered splitters
     */
    public static void clearSplitters() {
        splitters.clear();
    }
}