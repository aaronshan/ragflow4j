package com.ragflow4j.server.service;

import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 知识库管理服务接口
 * 定义知识库元信息的核心业务操作
 */
public interface KnowledgeService {
    
    /**
     * 保存知识库
     * 
     * @param knowledge 知识库对象
     * @return 保存后的知识库对象
     */
    Knowledge saveKnowledge(Knowledge knowledge);
    
    /**
     * 根据ID查找知识库
     * 
     * @param id 知识库ID
     * @return 知识库对象（可能为空）
     */
    Optional<Knowledge> findKnowledgeById(Long id);
    
    /**
     * 根据知识库类型查找知识库
     * 
     * @param type 知识库类型
     * @return 知识库列表
     */
    List<Knowledge> findKnowledgesByType(String type);
    
    /**
     * 根据名称关键词搜索知识库
     * 
     * @param keyword 名称关键词
     * @return 知识库列表
     */
    List<Knowledge> searchKnowledgesByName(String keyword);
    
    /**
     * 分页获取所有知识库
     * 
     * @param pageable 分页参数
     * @return 分页知识库结果
     */
    Page<Knowledge> getAllKnowledges(Pageable pageable);
    
    /**
     * 删除知识库
     * 
     * @param id 知识库ID
     */
    void deleteKnowledge(Long id);
    
    /**
     * 更新知识库
     * 
     * @param id 知识库ID
     * @param knowledge 更新的知识库内容
     * @return 更新后的知识库对象
     */
    Knowledge updateKnowledge(Long id, Knowledge knowledge);
    
    /**
     * 查找用户拥有的知识库
     * 
     * @param owner 知识库拥有者
     * @return 知识库列表
     */
    List<Knowledge> findKnowledgesByOwner(User owner);
    
    /**
     * 查找用户有权访问的知识库
     * 
     * @param user 授权用户
     * @return 知识库列表
     */
    List<Knowledge> findKnowledgesByAuthorizedUser(User user);
    
    /**
     * 授权用户访问知识库
     * 
     * @param knowledgeBaseId 知识库ID
     * @param userId 用户ID
     */
    void authorizeUser(Long knowledgeBaseId, UUID userId);
    
    /**
     * 撤销用户访问知识库的权限
     * 
     * @param knowledgeBaseId 知识库ID
     * @param userId 用户ID
     */
    void revokeUserAuthorization(Long knowledgeBaseId, UUID userId);
}