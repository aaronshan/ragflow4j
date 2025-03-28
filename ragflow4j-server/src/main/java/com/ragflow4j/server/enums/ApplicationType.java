package com.ragflow4j.server.enums;

/**
 * 应用类型枚举
 */
public enum ApplicationType {
    /**
     * 通用型对话应用
     * 可以自定义prompt，任意关联知识库、技能和工作流，配置灵活度更高
     */
    GENERAL_CONVERSATION,

    /**
     * FAQ型对话应用
     * 关联FAQ型知识，适用于回答较为固定、可控性要求极高的场景
     */
    FAQ,

    /**
     * 工作流型对话应用
     * 100%触发应用关联的唯一工作流，适用于需要精确执行工作流的场景
     */
    WORKFLOW
}