package com.ragflow4j.server.service;

import com.ragflow4j.server.dto.WorkflowDTO;
import java.util.List;

/**
 * 工作流管理服务接口
 */
public interface WorkflowService {
    /**
     * 创建工作流
     *
     * @param workflowDTO 工作流数据传输对象
     * @return 创建后的工作流数据传输对象
     */
    WorkflowDTO createWorkflow(WorkflowDTO workflowDTO);

    /**
     * 更新工作流
     *
     * @param id 工作流ID
     * @param workflowDTO 工作流数据传输对象
     * @return 更新后的工作流数据传输对象
     */
    WorkflowDTO updateWorkflow(Long id, WorkflowDTO workflowDTO);

    /**
     * 删除工作流
     *
     * @param id 工作流ID
     */
    void deleteWorkflow(Long id);

    /**
     * 获取工作流详情
     *
     * @param id 工作流ID
     * @return 工作流数据传输对象
     */
    WorkflowDTO getWorkflow(Long id);

    /**
     * 获取所有工作流
     *
     * @return 工作流数据传输对象列表
     */
    List<WorkflowDTO> getAllWorkflows();

    /**
     * 添加应用到工作流
     *
     * @param workflowId 工作流ID
     * @param applicationId 应用ID
     */
    void addApplicationToWorkflow(Long workflowId, Long applicationId);

    /**
     * 从工作流中移除应用
     *
     * @param workflowId 工作流ID
     * @param applicationId 应用ID
     */
    void removeApplicationFromWorkflow(Long workflowId, Long applicationId);

    /**
     * 根据名称关键词搜索工作流
     *
     * @param keyword 名称关键词
     * @return 工作流数据传输对象列表
     */
    List<WorkflowDTO> searchWorkflowsByName(String keyword);
}