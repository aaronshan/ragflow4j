package com.ragflow4j.server.repository;

import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 知识库数据访问层接口
 */
@Repository
public interface KnowledgeRepository extends JpaRepository<Knowledge, Long> {
    
    /**
     * 根据知识库名称查找知识库
     * 
     * @param name 知识库名称
     * @return 知识库列表
     */
    List<Knowledge> findByNameContaining(String name);
    
    /**
     * 根据知识库类型查找知识库
     * 
     * @param type 知识库类型
     * @return 知识库列表
     */
    List<Knowledge> findByType(String type);
    
    /**
     * 查找用户拥有的知识库
     * 
     * @param owner 知识库拥有者
     * @return 知识库列表
     */
    List<Knowledge> findByOwner(User owner);
    
    /**
     * 查找用户有权访问的知识库
     * 
     * @param user 授权用户
     * @return 知识库列表
     */
    List<Knowledge> findByAuthorizedUsersContaining(User user);
}