package com.ragflow4j.core.splitter;

import java.util.List;

/**
 * Document splitter interface for splitting documents into smaller chunks.
 */
public interface DocumentSplitter {
    
    /**
     * Split the document content into chunks
     *
     * @param content The document content to split
     * @return List of document chunks
     */
    List<String> split(String content);
    
    /**
     * Check if this splitter supports the given content type
     *
     * @param contentType The content type to check
     * @return true if this splitter supports the content type, false otherwise
     */
    boolean supports(String contentType);
    
    /**
     * Get splitter configuration
     *
     * @return Current splitter configuration
     */
    SplitterConfig getConfig();
}