package com.ragflow4j.server.repository;

import com.ragflow4j.server.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 文档数据访问层接口
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    /**
     * 根据文档类型查找文档
     * 
     * @param documentType 文档类型
     * @return 文档列表
     */
    List<Document> findByDocumentType(String documentType);
    
    /**
     * 根据标题模糊查询文档
     * 
     * @param titleKeyword 标题关键词
     * @return 文档列表
     */
    List<Document> findByTitleContaining(String titleKeyword);
    
    /**
     * 根据向量ID查找文档
     * 
     * @param vectorId 向量ID
     * @return 文档对象
     */
    Document findByVectorId(String vectorId);
}