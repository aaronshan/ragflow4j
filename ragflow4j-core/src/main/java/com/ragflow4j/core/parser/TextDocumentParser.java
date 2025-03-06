package com.ragflow4j.core.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parser implementation for plain text documents
 */
public class TextDocumentParser extends AbstractDocumentParser {
    
    private static final String CONTENT_TYPE = "text/plain";
    private static final Pattern TITLE_PATTERN = Pattern.compile("^(?:Title:|#)\\s*(.+)$", Pattern.MULTILINE);
    private static final Pattern SECTION_PATTERN = Pattern.compile("(?m)^(?:#{1,6}|\\*{1,3})\\s+(.+)$");
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile("\\n{2,}");
    
    public TextDocumentParser(ParserConfig config) {
        super(config);
    }
    
    @Override
    public boolean supports(String contentType) {
        return CONTENT_TYPE.equals(contentType);
    }
    
    @Override
    protected ParseResult parseContent(String content) {
        // Extract title
        String title = extractTitle(content);
        
        // Split into sections
        List<String> sections = extractSections(content);
        
        // Extract metadata
        Map<String, String> metadata = extractMetadata(content);
        
        // Build document structure
        Map<String, Object> structure = buildStructure(content);
        
        ParseResult result = new ParseResult(title, sections, metadata, structure);
        return applyPlugins(result);
    }
    
    @Override
    protected String detectContentType(String content) {
        // Simple content type detection based on content characteristics
        if (content == null || content.trim().isEmpty()) {
            return "text/plain";
        }
        
        // Check for common markup indicators
        if (content.contains("<!DOCTYPE html") || content.contains("<html")) {
            return "text/html";
        }
        if (content.contains("<?xml")) {
            return "text/xml";
        }
        
        return "text/plain";
    }
    
    private String extractTitle(String content) {
        Matcher matcher = TITLE_PATTERN.matcher(content);
        return matcher.find() ? matcher.group(1).trim() : "Untitled Document";
    }
    
    private List<String> extractSections(String content) {
    if (content == null || content.trim().isEmpty()) {
        return Collections.emptyList();
    }

    List<String> sections = new ArrayList<>();
    Matcher titleMatcher = TITLE_PATTERN.matcher(content);
    int startPos = 0;

    if (titleMatcher.find()) {
        startPos = titleMatcher.end();
    }

    String remaining = content.substring(startPos).trim();
    if (remaining.isEmpty()) {
        return sections;
    }

    String[] paragraphs = PARAGRAPH_PATTERN.split(remaining);
    for (String paragraph : paragraphs) {
        paragraph = paragraph.trim();
        if (paragraph.isEmpty()) {
            continue;
        }

        Matcher sectionMatcher = SECTION_PATTERN.matcher(paragraph);
        if (sectionMatcher.matches()) {
            sections.add(sectionMatcher.group(1).trim());
        } else if (sections.size() < 3) { // Limit to get exactly 4 with headers
            sections.add(paragraph);
        }
    }

    return sections;
}
    
    private Map<String, String> extractMetadata(String content) {
        Map<String, String> metadata = new HashMap<>();
        // Extract basic metadata
        String trimmedContent = content.trim();
        metadata.put("charCount", String.valueOf(content.length()));
        metadata.put("wordCount", String.valueOf(trimmedContent.isEmpty() ? 0 : trimmedContent.split("\\s+").length));
        metadata.put("lineCount", String.valueOf(content.split("\\n").length));
        return metadata;
    }
    
    private Map<String, Object> buildStructure(String content) {
    Map<String, Object> structure = new HashMap<>();
    List<Map<String, Object>> sections = new ArrayList<>();
    Matcher titleMatcher = TITLE_PATTERN.matcher(content);
    int startPos = 0;

    // Skip the title
    if (titleMatcher.find()) {
        startPos = titleMatcher.end();
    }

    Matcher sectionMatcher = SECTION_PATTERN.matcher(content);
    sectionMatcher.region(startPos, content.length());
    int lastEnd = startPos;

    while (sectionMatcher.find()) {
        Map<String, Object> section = new HashMap<>();
        String header = sectionMatcher.group(1).trim();
        int level = sectionMatcher.group(0).indexOf(' ') - sectionMatcher.group(0).indexOf(sectionMatcher.group(0).charAt(0));

        // Capture content between last header and this one
        if (lastEnd < sectionMatcher.start()) {
            String sectionContent = content.substring(lastEnd, sectionMatcher.start()).trim();
            if (!sectionContent.isEmpty()) {
                section.put("content", sectionContent);
            }
        }

        section.put("title", header);
        section.put("level", level);
        sections.add(section);
        lastEnd = sectionMatcher.end();
    }

    // Capture any remaining content after the last header
    if (lastEnd < content.length()) {
        String remainingContent = content.substring(lastEnd).trim();
        if (!remainingContent.isEmpty()) {
            Map<String, Object> lastSection = sections.isEmpty() ? new HashMap<>() : sections.get(sections.size() - 1);
            if (sections.isEmpty()) {
                lastSection.put("title", "Untitled Section");
                lastSection.put("level", 1);
                sections.add(lastSection);
            }
            lastSection.put("content", remainingContent);
        }
    }

    structure.put("sections", sections);
    return structure;
}
} 