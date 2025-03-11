package com.ragflow4j.common.llm;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM统一调用接口
 * 定义了与大语言模型交互的标准方法
 */
public interface LLMClient {
    /**
     * 同步调用LLM模型
     *
     * @param prompt 输入提示
     * @param options 调用选项
     * @return 模型响应结果
     */
    String complete(String prompt, Map<String, Object> options);

    /**
     * 异步调用LLM模型
     *
     * @param prompt 输入提示
     * @param options 调用选项
     * @return 异步响应结果
     */
    CompletableFuture<String> completeAsync(String prompt, Map<String, Object> options);

    /**
     * 获取模型配置信息
     *
     * @return 当前模型配置
     */
    LLMConfig getConfig();

    /**
     * 更新模型配置
     *
     * @param config 新的配置信息
     */
    void updateConfig(LLMConfig config);
}