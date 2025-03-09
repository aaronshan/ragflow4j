package com.ragflow4j.core.vectorstore;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 向量存储接口，定义向量数据库的基本操作
 */
public interface VectorStore {
    /**
     * 添加向量
     *
     * @param vectors 向量列表
     * @param metadata 元数据列表
     * @return 添加结果
     */
    CompletableFuture<Boolean> addVectors(List<float[]> vectors, List<String> metadata);

    /**
     * 批量添加向量
     *
     * @param vectors 向量列表
     * @param metadata 元数据列表
     * @param batchSize 批次大小
     * @return 添加结果
     */
    CompletableFuture<Boolean> addVectorsBatch(List<float[]> vectors, List<String> metadata, int batchSize);

    /**
     * 搜索相似向量
     *
     * @param queryVector 查询向量
     * @param topK 返回结果数量
     * @return 搜索结果
     */
    CompletableFuture<List<SearchResult>> search(float[] queryVector, int topK);

    /**
     * 删除向量
     *
     * @param ids 向量ID列表
     * @return 删除结果
     */
    CompletableFuture<Boolean> deleteVectors(List<String> ids);

    /**
     * 更新向量
     *
     * @param id 向量ID
     * @param vector 新向量
     * @param metadata 新元数据
     * @return 更新结果
     */
    CompletableFuture<Boolean> updateVector(String id, float[] vector, String metadata);
}