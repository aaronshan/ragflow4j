package com.ragflow4j.core.parser;

import java.util.Map;

/**
 * Parser configuration class
 */
public class ParserConfig {
    private final int maxTokens;
    private final boolean preserveFormatting;
    private final Map<String, String> options;
    
    public ParserConfig(int maxTokens, boolean preserveFormatting, Map<String, String> options) {
        this.maxTokens = maxTokens;
        this.preserveFormatting = preserveFormatting;
        this.options = options;
    }
    
    public int getMaxTokens() {
        return maxTokens;
    }
    
    public boolean isPreserveFormatting() {
        return preserveFormatting;
    }
    
    public Map<String, String> getOptions() {
        return options;
    }
} 