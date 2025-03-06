package com.ragflow4j.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Abstract base class for document parsers implementing chain of responsibility pattern
 */
public abstract class AbstractDocumentParser implements DocumentParser {
    
    private final ParserConfig config;
    private DocumentParser nextParser;
    protected final List<ParserPlugin> plugins;
    
    protected AbstractDocumentParser(ParserConfig config) {
        this.config = config;
        this.plugins = new ArrayList<>();
    }
    
    /**
     * Set next parser in the chain
     *
     * @param nextParser The next parser to process content
     * @return The next parser
     */
    @Override
    public DocumentParser setNext(DocumentParser nextParser) {
        this.nextParser = nextParser;
        return nextParser;
    }
    
    /**
     * Add a parser plugin
     *
     * @param plugin The plugin to add
     */
    public void addPlugin(ParserPlugin plugin) {
        plugins.add(plugin);
    }
    
    @Override
    public ParseResult parse(String content) {
        if (supports(detectContentType(content))) {
            return parseContent(content);
        }
        
        if (nextParser != null) {
            return nextParser.parse(content);
        }
        
        throw new UnsupportedOperationException("No parser found for content");
    }
    
    @Override
    public ParserConfig getConfig() {
        return config;
    }
    
    /**
     * Parse the content using this parser's implementation
     *
     * @param content The content to parse
     * @return The parsed result
     */
    protected abstract ParseResult parseContent(String content);
    
    /**
     * Detect content type from the content
     *
     * @param content The content to analyze
     * @return The detected content type
     */
    protected abstract String detectContentType(String content);
    
    /**
     * Apply all registered plugins to the parse result
     *
     * @param result The parse result to process
     * @return The processed parse result
     */
    protected ParseResult applyPlugins(ParseResult result) {
        ParseResult processedResult = result;
        for (ParserPlugin plugin : plugins) {
            processedResult = plugin.process(processedResult);
        }
        return processedResult;
    }
} 