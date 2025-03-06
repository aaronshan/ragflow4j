package com.ragflow4j.core.parser;

/**
 * Document parser interface for parsing different types of documents.
 */
public interface DocumentParser {
    
    /**
     * Parse document content into structured format
     *
     * @param content The document content to parse
     * @return Parsed document structure with metadata
     */
    ParseResult parse(String content);
    
    /**
     * Check if this parser supports the given content type
     *
     * @param contentType The content type to check
     * @return true if this parser supports the content type, false otherwise
     */
    boolean supports(String contentType);
    
    /**
     * Get parser configuration
     *
     * @return Current parser configuration
     */
    ParserConfig getConfig();
    
    /**
     * Set next parser in the chain
     *
     * @param nextParser The next parser to process content
     * @return The next parser
     */
    DocumentParser setNext(DocumentParser nextParser);
} 