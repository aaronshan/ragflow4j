package com.ragflow4j.core.retriever;

import java.util.Map;

/**
 * 检索结果模型类
 */
public class RetrievalResult {
    /**
     * 检索内容
     */
    private String content;

    /**
     * 相似度分数
     */
    private double score;

    /**
     * 元数据信息
     */
    private Map<String, Object> metadata;

    /**
     * 检索来源类型
     */
    private RetrieverType sourceType;

    public RetrievalResult(String content, double score, Map<String, Object> metadata, RetrieverType sourceType) {
        this.content = content;
        this.score = score;
        this.metadata = metadata;
        this.sourceType = sourceType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public RetrieverType getSourceType() {
        return sourceType;
    }

    public void setSourceType(RetrieverType sourceType) {
        this.sourceType = sourceType;
    }
}