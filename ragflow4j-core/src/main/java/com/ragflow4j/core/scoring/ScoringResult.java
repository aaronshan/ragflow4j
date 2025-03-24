package com.ragflow4j.core.scoring;

/**
 * 评分结果类
 * 封装文档片段的评分信息
 */
public class ScoringResult {
    private final String document;
    private final double score;
    private final String metadata;

    public ScoringResult(String document, double score, String metadata) {
        this.document = document;
        this.score = score;
        this.metadata = metadata;
    }

    public ScoringResult(String document, double score) {
        this(document, score, null);
    }

    /**
     * 获取文档内容
     *
     * @return 文档内容
     */
    public String getDocument() {
        return document;
    }

    /**
     * 获取评分值
     *
     * @return 评分值
     */
    public double getScore() {
        return score;
    }

    /**
     * 获取元数据信息
     *
     * @return 元数据信息
     */
    public String getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return String.format("ScoringResult{document='%s', score=%f, metadata='%s'}",
                document, score, metadata);
    }
}