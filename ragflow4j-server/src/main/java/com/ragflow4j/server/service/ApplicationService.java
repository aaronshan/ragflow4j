package com.ragflow4j.server.service;

import com.ragflow4j.server.dto.ApplicationDTO;
import com.ragflow4j.server.entity.Application;
import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.entity.Skill;
import com.ragflow4j.server.entity.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * 应用管理服务接口
 */
public interface ApplicationService {
    /**
     * 创建应用
     *
     * @param dto 应用信息
     * @param userId 创建者ID
     * @return 创建的应用
     */
    Application createApplication(ApplicationDTO dto, Long userId);

    /**
     * 更新应用
     *
     * @param id 应用ID
     * @param dto 应用信息
     * @param userId 更新者ID
     * @return 更新后的应用
     */
    Application updateApplication(Long id, ApplicationDTO dto, Long userId);

    /**
     * 删除应用
     *
     * @param id 应用ID
     */
    void deleteApplication(Long id);

    /**
     * 获取应用详情
     *
     * @param id 应用ID
     * @return 应用详情
     */
    Application getApplication(Long id);

    /**
     * 分页查询应用列表
     *
     * @param pageable 分页参数
     * @return 应用列表
     */
    Page<Application> listApplications(Pageable pageable);

    /**
     * 启用/禁用应用
     *
     * @param id 应用ID
     * @param enabled 是否启用
     * @param userId 操作者ID
     * @return 更新后的应用
     */
    Application toggleApplicationStatus(Long id, boolean enabled, Long userId);

    /**
     * 添加技能到应用
     *
     * @param applicationId 应用ID
     * @param skillId 技能ID
     */
    void addSkillToApplication(Long applicationId, Long skillId);

    /**
     * 从应用中移除技能
     *
     * @param applicationId 应用ID
     * @param skillId 技能ID
     */
    void removeSkillFromApplication(Long applicationId, Long skillId);

    /**
     * 添加工作流到应用
     *
     * @param applicationId 应用ID
     * @param workflowId 工作流ID
     */
    void addWorkflowToApplication(Long applicationId, Long workflowId);

    /**
     * 从应用中移除工作流
     *
     * @param applicationId 应用ID
     * @param workflowId 工作流ID
     */
    void removeWorkflowFromApplication(Long applicationId, Long workflowId);

    /**
     * 添加知识库到应用
     *
     * @param applicationId 应用ID
     * @param knowledgeBaseId 知识库ID
     */
    void addKnowledgeBaseToApplication(Long applicationId, Long knowledgeBaseId);

    /**
     * 从应用中移除知识库
     *
     * @param applicationId 应用ID
     * @param knowledgeBaseId 知识库ID
     */
    void removeKnowledgeBaseFromApplication(Long applicationId, Long knowledgeBaseId);

    /**
     * 从应用中移除知识库
     *
     * @param applicationId 应用ID
     * @param knowledgeId 知识库ID
     */
    void removeKnowledge(Long applicationId, Long knowledgeId);

    /**
     * 根据名称关键词搜索应用
     *
     * @param keyword 名称关键词
     * @return 应用列表
     */
    List<Application> searchApplicationsByName(String keyword);

    /**
     * 根据类型查找应用
     *
     * @param type 应用类型
     * @return 应用列表
     */
    List<Application> findApplicationsByType(String type);

    /**
     * 获取所有应用（分页）
     *
     * @param pageable 分页参数
     * @return 应用列表
     */
    Page<Application> getAllApplications(Pageable pageable);

    Set<Knowledge> getKnowledges(Long applicationId);

    Set<Skill> getSkills(Long applicationId);

    Set<Workflow> getWorkflows(Long applicationId);
}