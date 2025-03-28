package com.ragflow4j.server.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * 技能数据传输对象
 */
@Data
public class SkillDTO {
    /**
     * 技能ID（创建时为null）
     */
    private Long id;

    /**
     * 技能名称
     */
    @NotBlank(message = "技能名称不能为空")
    private String name;

    /**
     * 技能描述
     */
    private String description;

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