package com.ragflow4j.core.splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Text splitter implementation that supports both paragraph and sentence splitting.
 */
public class TextSplitter implements DocumentSplitter {
    private static final String CONTENT_TYPE = "text/plain";
    private final SplitterConfig config;
    
    public TextSplitter(SplitterConfig config) {
        this.config = config;
    }
    
    @Override
    public List<String> split(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> result;
        String paragraphMarker = config.getOption("paragraphMarker");
        String sentenceEndMarkers = config.getOption("sentenceEndMarkers");
        
        // First split by paragraphs or sentences
        if (content.contains(paragraphMarker)) {
            result = splitByParagraphs(content.trim(), paragraphMarker);
        } else {
            result = splitBySentence(content.trim(), sentenceEndMarkers);
        }
        
        // Then apply length constraints if needed
        if (result.size() == 1 && result.get(0).length() > config.getMaxChunkSize()) {
            result = applyLengthConstraints(result);
        }
        
        return result;
    }
    
    @Override
    public boolean supports(String contentType) {
        return CONTENT_TYPE.equals(contentType);
    }
    
    @Override
    public SplitterConfig getConfig() {
        return config;
    }
    
    private List<String> splitByParagraphs(String content, String paragraphMarker) {
        List<String> paragraphs = new ArrayList<>();
        String[] parts = content.split(Pattern.quote(paragraphMarker));
        
        for (String part : parts) {
            if (!part.trim().isEmpty()) {
                paragraphs.add(part.trim());
            }
        }
        
        return paragraphs;
    }
    
    private List<String> splitBySentence(String content, String endMarkers) {
        List<String> sentences = new ArrayList<>();
        StringBuilder currentSentence = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            currentSentence.append(c);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            }
            
            if (!inQuotes && endMarkers.indexOf(c) != -1) {
                // Look ahead for quotation marks or closing brackets
                while (i + 1 < content.length() && 
                       (Character.isWhitespace(content.charAt(i + 1)) || 
                        isClosingPunctuation(content.charAt(i + 1)))) {
                    currentSentence.append(content.charAt(++i));
                }
                
                String sentence = currentSentence.toString().trim();
                if (!sentence.isEmpty()) {
                    sentences.add(sentence);
                    currentSentence = new StringBuilder();
                }
            }
        }
        
        // Add any remaining content as a sentence
        String remaining = currentSentence.toString().trim();
        if (!remaining.isEmpty()) {
            sentences.add(remaining);
        }
        
        return sentences;
    }
    
    private boolean isClosingPunctuation(char c) {
        return c == '"' || c == '\'' || c == ')' || c == ']' || c == '}';
    }
    
    private List<String> applyLengthConstraints(List<String> chunks) {
        List<String> result = new ArrayList<>();
        
        for (String chunk : chunks) {
            if (chunk.length() <= config.getMaxChunkSize()) {
                result.add(chunk);
                continue;
            }
            
            // Split large chunks into smaller pieces
            int start = 0;
            while (start < chunk.length()) {
                int end = Math.min(start + config.getMaxChunkSize(), chunk.length());
                // Try to find a space to break at if possible
                if (end < chunk.length() && !Character.isWhitespace(chunk.charAt(end))) {
                    int lastSpace = chunk.lastIndexOf(' ', end);
                    if (lastSpace > start) {
                        end = lastSpace;
                    }
                }
                String subChunk = chunk.substring(start, end).trim();
                if (subChunk.length() >= config.getMinChunkSize()) {
                    result.add(subChunk);
                }
                start = end + 1;
            }
        }
        
        return result;
    }
}