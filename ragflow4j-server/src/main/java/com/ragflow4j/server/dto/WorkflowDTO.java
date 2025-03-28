package com.ragflow4j.server.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * 工作流数据传输对象
 */
@Data
public class WorkflowDTO {
    /**
     * 工作流ID（创建时为null）
     */
    private Long id;

    /**
     * 工作流名称
     */
    @NotBlank(message = "工作流名称不能为空")
    private String name;

    /**
     * 工作流描述
     */
    private String description;

    /**
     * 工作流配置
     */
    private String configuration;

    /**
     * 创建者ID
     */
    private Long createdBy;

    /**
     * 更新者ID
     */
    private Long updatedBy;

    /**
     * 关联的应用ID集合
     */
    private Set<Long> applicationIds;
}