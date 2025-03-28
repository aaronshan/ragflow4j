package com.ragflow4j.server.service;

import com.ragflow4j.server.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 文档管理服务接口
 * 定义文档的核心业务操作
 */
public interface DocumentService {
    
    /**
     * 保存文档到知识库
     * 
     * @param document 文档对象
     * @return 保存后的文档对象
     */
    Document saveDocument(Document document);
    
    /**
     * 根据ID查找文档
     * 
     * @param id 文档ID
     * @return 文档对象（可能为空）
     */
    Optional<Document> findDocumentById(UUID id);
    
    /**
     * 根据文档类型查找文档
     * 
     * @param documentType 文档类型
     * @return 文档列表
     */
    List<Document> findDocumentsByType(String documentType);
    
    /**
     * 根据标题关键词搜索文档
     * 
     * @param keyword 标题关键词
     * @return 文档列表
     */
    List<Document> searchDocumentsByTitle(String keyword);
    
    /**
     * 分页获取所有文档
     * 
     * @param pageable 分页参数
     * @return 分页文档结果
     */
    Page<Document> getAllDocuments(Pageable pageable);
    
    /**
     * 删除文档
     * 
     * @param id 文档ID
     */
    void deleteDocument(UUID id);
    
    /**
     * 更新文档
     * 
     * @param id 文档ID
     * @param document 更新的文档内容
     * @return 更新后的文档对象
     */
    Document updateDocument(UUID id, Document document);
    
    /**
     * 根据向量ID查找文档
     * 
     * @param vectorId 向量ID
     * @return 文档对象（可能为空）
     */
    Optional<Document> findDocumentByVectorId(String vectorId);
}