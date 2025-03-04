package com.ragflow4j.core.loader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * Document loader interface for loading different types of documents.
 */
public interface DocumentLoader {
    
    /**
     * Synchronously load document content from a file path
     *
     * @param path The path to the document
     * @return The loaded document content
     * @throws IOException If an error occurs during loading
     */
    String load(Path path) throws IOException;
    
    /**
     * Asynchronously load document content from a file path
     *
     * @param path The path to the document
     * @return A CompletableFuture containing the loaded document content
     */
    CompletableFuture<String> loadAsync(Path path);
    
    /**
     * Check if this loader supports the given file type
     *
     * @param path The path to check
     * @return true if this loader supports the file type, false otherwise
     */
    boolean supports(Path path);
} 