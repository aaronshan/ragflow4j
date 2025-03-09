package com.ragflow4j.core.vectorstore;

/**
 * 向量搜索结果
 */
public class SearchResult {
    public String id;
    public Object data;
    public SearchResult(String id, Object data) {
        this.id = id;
        this.data = data;
    }
}