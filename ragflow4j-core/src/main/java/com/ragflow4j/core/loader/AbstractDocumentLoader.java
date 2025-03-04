package com.ragflow4j.core.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Abstract base class for document loaders providing common functionality
 */
public abstract class AbstractDocumentLoader implements DocumentLoader {
    
    protected static final ExecutorService executor = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors()
    );
    
    @Override
    public CompletableFuture<String> loadAsync(Path path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return load(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load document: " + path, e);
            }
        }, executor);
    }
    
    /**
     * Validate the input file
     *
     * @param path The path to validate
     * @throws IOException If the file is invalid
     */
    protected void validateFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }
        if (!Files.isRegularFile(path)) {
            throw new IOException("Path is not a regular file: " + path);
        }
        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + path);
        }
    }
} 