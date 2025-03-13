package com.ragflow4j.core.retriever;

/**
 * 检索器类型枚举
 */
public enum RetrieverType {
    /**
     * 向量数据库检索
     */
    VECTOR,

    /**
     * Web搜索检索
     */
    WEB_SEARCH,

    /**
     * 混合检索
     */
    HYBRID
}