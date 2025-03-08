package com.ragflow4j.core.embedding;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 文档向量化服务接口
 * 负责将文本内容转换为向量表示
 */
public interface DocumentEmbedding {
    
    /**
     * 将单个文本转换为向量
     *
     * @param text 输入文本
     * @return 向量表示（浮点数数组）
     */
    float[] embed(String text);

    /**
     * 异步将单个文本转换为向量
     *
     * @param text 输入文本
     * @return 包含向量表示的CompletableFuture
     */
    CompletableFuture<float[]> embedAsync(String text);

    /**
     * 批量将多个文本转换为向量
     *
     * @param texts 输入文本列表
     * @return 向量表示列表
     */
    List<float[]> embedBatch(List<String> texts);

    /**
     * 异步批量将多个文本转换为向量
     *
     * @param texts 输入文本列表
     * @return 包含向量表示列表的CompletableFuture
     */
    CompletableFuture<List<float[]>> embedBatchAsync(List<String> texts);

    /**
     * 获取向量维度
     *
     * @return 向量维度
     */
    int getDimension();

    /**
     * 获取模型名称
     *
     * @return 模型名称
     */
    String getModelName();
}