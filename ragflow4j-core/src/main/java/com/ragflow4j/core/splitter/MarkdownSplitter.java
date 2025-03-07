package com.ragflow4j.core.splitter;

import java.util.ArrayList;
import java.util.List;

public class MarkdownSplitter implements DocumentSplitter {
    private static final String CONTENT_TYPE = "text/markdown";
    private final SplitterConfig config;
    
    public MarkdownSplitter(SplitterConfig config) {
        this.config = config;
    }
    
    @Override
    public List<String> split(String content) {
        // 基本实现，后续可以根据需要扩展
        if (content == null || content.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // 暂时使用简单的段落分割
        String[] paragraphs = content.split("\n\n");
        List<String> result = new ArrayList<>();
        
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                result.add(paragraph.trim());
            }
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
}