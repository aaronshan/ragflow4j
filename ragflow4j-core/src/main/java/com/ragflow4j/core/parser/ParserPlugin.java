package com.ragflow4j.core.parser;

/**
 * Interface for parser plugins that can modify parse results
 */
public interface ParserPlugin {
    /**
     * Process a parse result
     *
     * @param result The parse result to process
     * @return The processed parse result
     */
    ParseResult process(ParseResult result);
} 