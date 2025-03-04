package com.ragflow4j.core.loader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Factory class for creating and managing document loaders
 */
public class DocumentLoaderFactory {
    
    private static final List<DocumentLoader> loaders = new ArrayList<>();
    
    static {
        // Register default loaders
        registerLoader(new TextDocumentLoader());
        registerLoader(new PdfDocumentLoader());
    }
    
    /**
     * Register a new document loader
     *
     * @param loader The loader to register
     * @throws NullPointerException if the loader is null
     */
    public static void registerLoader(DocumentLoader loader) {
        Objects.requireNonNull(loader, "Loader cannot be null");
        loaders.add(loader);
    }
    
    /**
     * Get an appropriate loader for the given file path
     *
     * @param path The path to get a loader for
     * @return Optional containing the loader if one is found
     */
    public static Optional<DocumentLoader> getLoader(Path path) {
        return loaders.stream()
            .filter(loader -> loader.supports(path))
            .findFirst();
    }
    
    /**
     * Clear all registered loaders
     */
    public static void clearLoaders() {
        loaders.clear();
    }
    
    /**
     * Get the number of registered loaders
     *
     * @return The number of registered loaders
     */
    public static int getLoaderCount() {
        return loaders.size();
    }
} 