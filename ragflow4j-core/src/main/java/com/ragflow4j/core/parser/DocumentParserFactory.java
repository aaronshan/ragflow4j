package com.ragflow4j.core.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating and managing document parsers
 */
public class DocumentParserFactory {
    
    private static final Map<String, DocumentParser> parsers = new HashMap<>();
    private static final ParserConfig defaultConfig;
    
    static {
        // Initialize default configuration
        Map<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("encoding", "UTF-8");
        defaultOptions.put("maxSectionLength", "1000");
        
        defaultConfig = new ParserConfig(
            4096,  // maxTokens
            true,  // preserveFormatting
            defaultOptions
        );
        
        // Register default parsers
        registerParser("text/plain", new TextDocumentParser(defaultConfig));
        registerParser("text/html", new HtmlDocumentParser(defaultConfig));
    }
    
    /**
     * Register a new parser for a content type
     *
     * @param contentType The content type
     * @param parser The parser implementation
     */
    public static void registerParser(String contentType, DocumentParser parser) {
        parsers.put(contentType, parser);
    }
    
    /**
     * Get a parser for the given content type
     *
     * @param contentType The content type
     * @return The appropriate parser
     * @throws UnsupportedOperationException if no parser is found
     */
    public static DocumentParser getParser(String contentType) {
        DocumentParser parser = parsers.get(contentType);
        if (parser == null) {
            throw new UnsupportedOperationException("No parser found for content type: " + contentType);
        }
        return parser;
    }
    
    /**
     * Create a parser chain for handling multiple content types
     *
     * @return The head of the parser chain
     */
    public static DocumentParser createParserChain() {
        DocumentParser textParser = new TextDocumentParser(defaultConfig);
        DocumentParser htmlParser = new HtmlDocumentParser(defaultConfig);
        
        textParser.setNext(htmlParser);
        
        return textParser;
    }
    
    /**
     * Get the default parser configuration
     *
     * @return The default configuration
     */
    public static ParserConfig getDefaultConfig() {
        return defaultConfig;
    }
    
    /**
     * Clear all registered parsers
     */
    public static void clearParsers() {
        parsers.clear();
    }
} 