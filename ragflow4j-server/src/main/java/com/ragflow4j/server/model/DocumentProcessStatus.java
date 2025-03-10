package com.ragflow4j.server.model;

/**
 * 文档处理状态枚举
 * 用于跟踪文档从上传到向量化的全过程
 */
public enum DocumentProcessStatus {
    /**
     * 已上传：文档刚上传到系统，尚未开始处理
     */
    UPLOADED,
    
    /**
     * 解析中：文档正在被解析器处理
     */
    PARSING,
    
    /**
     * 已解析：文档已被成功解析
     */
    PARSED,
    
    /**
     * 切分中：文档正在被分割成小块
     */
    SPLITTING,
    
    /**
     * 已切分：文档已被成功切分成小块
     */
    SPLIT,
    
    /**
     * 向量化中：文档内容正在被转换为向量
     */
    VECTORIZING,
    
    /**
     * 已向量化：文档内容已成功转换为向量并存储
     */
    VECTORIZED,
    
    /**
     * 处理失败：文档处理过程中出现错误
     */
    FAILED
}