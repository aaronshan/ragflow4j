package com.ragflow4j.server.service;

import com.ragflow4j.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户管理服务接口
 * 定义用户的核心业务操作
 */
public interface UserService {
    
    /**
     * 保存用户
     * 
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    User saveUser(User user);
    
    /**
     * 根据ID查找用户
     * 
     * @param id 用户ID
     * @return 用户对象（可能为空）
     */
    Optional<User> findById(UUID id);
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户对象（可能为空）
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     * 
     * @param email 邮箱
     * @return 用户对象（可能为空）
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 分页获取所有用户
     * 
     * @param pageable 分页参数
     * @return 分页用户结果
     */
    Page<User> getAllUsers(Pageable pageable);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    void deleteUser(UUID id);
    
    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param user 更新的用户内容
     * @return 更新后的用户对象
     */
    User updateUser(UUID id, User user);
    
    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @return 是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}