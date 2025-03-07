package com.ragflow4j.core.splitter;

import java.util.Map;
import java.util.HashMap;

/**
 * Configuration class for document splitters.
 */
public class SplitterConfig {
    private final int maxChunkSize;
    private final int minChunkSize;
    private final int overlap;
    private final Map<String, String> options;

    /**
     * Constructor for SplitterConfig
     *
     * @param maxChunkSize Maximum size of each chunk
     * @param minChunkSize Minimum size of each chunk
     * @param overlap Number of characters/tokens to overlap between chunks
     * @param options Additional configuration options
     */
    public SplitterConfig(int maxChunkSize, int minChunkSize, int overlap, Map<String, String> options) {
        this.maxChunkSize = maxChunkSize;
        this.minChunkSize = minChunkSize;
        this.overlap = overlap;
        this.options = new HashMap<>(options);
    }

    /**
     * Get maximum chunk size
     *
     * @return Maximum chunk size
     */
    public int getMaxChunkSize() {
        return maxChunkSize;
    }

    /**
     * Get minimum chunk size
     *
     * @return Minimum chunk size
     */
    public int getMinChunkSize() {
        return minChunkSize;
    }

    /**
     * Get overlap size
     *
     * @return Overlap size
     */
    public int getOverlap() {
        return overlap;
    }

    /**
     * Get additional options
     *
     * @return Map of additional options
     */
    public Map<String, String> getOptions() {
        return new HashMap<>(options);
    }

    /**
     * Get specific option value
     *
     * @param key Option key
     * @return Option value or null if not found
     */
    public String getOption(String key) {
        return options.get(key);
    }
}