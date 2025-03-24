package com.ragflow4j.core.scoring;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 内容评分服务接口
 * 提供异步的文档相关性评分功能
 */
public interface ScoringService {
    /**
     * 异步计算查询和文档片段的相关性分数
     *
     * @param query 查询文本
     * @param documents 待评分的文档片段列表
     * @return 返回CompletableFuture，包含每个文档片段的评分结果
     */
    CompletableFuture<List<ScoringResult>> score(String query, List<String> documents);

    /**
     * 批量异步计算查询和文档片段的相关性分数
     *
     * @param queries 查询文本列表
     * @param documents 待评分的文档片段列表
     * @return 返回CompletableFuture，包含每个查询对应的文档片段评分结果
     */
    CompletableFuture<List<List<ScoringResult>>> batchScore(List<String> queries, List<String> documents);

    /**
     * 清除评分缓存
     */
    void clearCache();
}