package com.ragflow4j.core.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser implementation for HTML documents
 */
public class HtmlDocumentParser extends AbstractDocumentParser {
    
    private static final String CONTENT_TYPE = "text/html";
    
    public HtmlDocumentParser(ParserConfig config) {
        super(config);
    }
    
    @Override
    public boolean supports(String contentType) {
        return CONTENT_TYPE.equals(contentType);
    }
    
    @Override
    protected ParseResult parseContent(String content) {
        Document doc = Jsoup.parse(content);
        
        // Extract title
        String title = extractTitle(doc);
        
        // Extract sections
        List<String> sections = extractSections(doc);
        
        // Extract metadata
        Map<String, String> metadata = extractMetadata(doc);
        
        // Build document structure
        Map<String, Object> structure = buildStructure(doc);
        
        ParseResult result = new ParseResult(title, sections, metadata, structure);
        return applyPlugins(result);
    }
    
    @Override
    protected String detectContentType(String content) {
        return content != null && 
               (content.contains("<!DOCTYPE html") || content.contains("<html")) ? 
               CONTENT_TYPE : "text/plain";
    }
    
    private String extractTitle(Document doc) {
        String title = doc.title();
        if (title == null || title.isEmpty()) {
            Element h1 = doc.selectFirst("h1");
            title = h1 != null ? h1.text() : "Untitled Document";
        }
        return title;
    }
    
    private List<String> extractSections(Document doc) {
        List<String> sections = new ArrayList<>();
        
        // Extract text from paragraphs
        Elements paragraphs = doc.select("p");
        for (Element p : paragraphs) {
            String text = p.text().trim();
            if (!text.isEmpty()) {
                sections.add(text);
            }
        }
        
        // Extract text from other content blocks
        Elements contentBlocks = doc.select("div, section, article");
        for (Element block : contentBlocks) {
            if (block.children().isEmpty()) {
                String text = block.text().trim();
                if (!text.isEmpty()) {
                    sections.add(text);
                }
            }
        }
        
        return sections;
    }
    
    private Map<String, String> extractMetadata(Document doc) {
        Map<String, String> metadata = new HashMap<>();
        
        // Extract meta tags
        Elements metaTags = doc.select("meta");
        for (Element meta : metaTags) {
            String name = meta.attr("name");
            String content = meta.attr("content");
            if (!name.isEmpty() && !content.isEmpty()) {
                metadata.put(name, content);
            }
        }
        
        // Add basic document info
        metadata.put("charset", doc.charset().name());
        metadata.put("baseUri", doc.baseUri());
        
        return metadata;
    }
    
    private Map<String, Object> buildStructure(Document doc) {
        Map<String, Object> structure = new HashMap<>();
        List<Map<String, Object>> sections = new ArrayList<>();
        
        // Process headings to build document structure
        for (int i = 1; i <= 6; i++) {
            Elements headings = doc.select("h" + i);
            for (Element heading : headings) {
                Map<String, Object> section = new HashMap<>();
                section.put("title", heading.text());
                section.put("level", i);
                
                // Get content until next heading
                StringBuilder content = new StringBuilder();
                Element next = heading.nextElementSibling();
                while (next != null && !next.tagName().matches("h[1-6]")) {
                    content.append(next.text()).append("\n");
                    next = next.nextElementSibling();
                }
                section.put("content", content.toString().trim());
                
                sections.add(section);
            }
        }
        
        structure.put("sections", sections);
        return structure;
    }
} 