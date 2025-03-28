package com.ragflow4j.server.repository;

import com.ragflow4j.server.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 工作流数据访问接口
 */
@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    /**
     * 根据名称关键词搜索工作流（不区分大小写）
     *
     * @param name 名称关键词
     * @return 工作流列表
     */
    List<Workflow> findByNameContainingIgnoreCase(String name);
}