package com.ragflow4j.server.dto;

import com.ragflow4j.server.enums.ApplicationType;
import com.ragflow4j.server.enums.PublishChannelType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 应用数据传输对象
 */
@Data
public class ApplicationDTO {
    /**
     * 应用ID（创建时为null）
     */
    private Long id;

    /**
     * 应用名称
     */
    @NotBlank(message = "应用名称不能为空")
    private String name;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用类型
     */
    @NotNull(message = "应用类型不能为空")
    private ApplicationType type;

    /**
     * 应用图标URL
     */
    private String iconUrl;

    /**
     * 发布渠道
     */
    private List<PublishChannelType> publishChannels;



    /**
     * 是否显示参考来源
     */
    private Boolean showReferences;

    /**
     * 是否启用语音输出
     */
    private Boolean enableVoiceOutput;

    /**
     * 会话超时时间（分钟）
     */
    private Integer sessionTimeout;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 关联的知识库ID集合
     */
    private List<Long> knowledgeIds;

    /**
     * 关联的技能ID集合
     */
    private List<Long> skillIds;

    /**
     * 关联的工作流ID集合
     */
    private List<Long> workflowIds;


}