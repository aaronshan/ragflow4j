package com.ragflow4j.core.retriever;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 内容检索器接口
 * 定义统一的检索接口，支持同步和异步检索方式
 */
public interface ContentRetriever {
    /**
     * 同步检索内容
     *
     * @param query 查询内容
     * @param topK 返回结果数量
     * @return 检索结果列表
     */
    List<RetrievalResult> retrieve(String query, int topK);

    /**
     * 异步检索内容
     *
     * @param query 查询内容
     * @param topK 返回结果数量
     * @return 异步检索结果
     */
    CompletableFuture<List<RetrievalResult>> retrieveAsync(String query, int topK);

    /**
     * 获取检索器类型
     *
     * @return 检索器类型
     */
    RetrieverType getType();
}