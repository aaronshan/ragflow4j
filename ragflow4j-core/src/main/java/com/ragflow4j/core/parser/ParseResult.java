package com.ragflow4j.core.parser;

import java.util.List;
import java.util.Map;

/**
 * Parse result class containing structured document content
 */
public class ParseResult {
    private final String title;
    private final List<String> sections;
    private final Map<String, String> metadata;
    private final Map<String, Object> structure;
    
    public ParseResult(String title, List<String> sections, Map<String, String> metadata, Map<String, Object> structure) {
        this.title = title;
        this.sections = sections;
        this.metadata = metadata;
        this.structure = structure;
    }
    
    public String getTitle() {
        return title;
    }
    
    public List<String> getSections() {
        return sections;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public Map<String, Object> getStructure() {
        return structure;
    }
} 