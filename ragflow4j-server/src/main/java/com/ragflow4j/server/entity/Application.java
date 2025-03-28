package com.ragflow4j.server.entity;

import com.ragflow4j.server.enums.ApplicationType;
import com.ragflow4j.server.enums.PublishChannelType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用实体类
 */
@Data
@Entity
@Table(name = "rf_application")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 应用名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationType type;

    /**
     * 应用图标URL
     */
    private String iconUrl;

    /**
     * 发布渠道
     */
    @ElementCollection
    @CollectionTable(name = "rf_application_publish_channels",
            joinColumns = @JoinColumn(name = "application_id"))
    @Column(name = "channel_type")
    @Enumerated(EnumType.STRING)
    private List<PublishChannelType> publishChannels;

    /**
     * 是否显示参考来源
     */
    private boolean showReferences = true;

    /**
     * 是否启用语音输出
     */
    private boolean enableVoiceOutput = false;

    /**
     * 会话超时时间（分钟）
     */
    private Integer sessionTimeout = 30;

    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建者ID
     */
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    /**
     * 更新者ID
     */
    @Column(nullable = false)
    private Long updatedBy;

    /**
     * 关联的知识库
     */
    @ManyToMany
    @JoinTable(
        name = "rf_application_knowledge_bases",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "knowledge_base_id")
    )
    private List<Knowledge> knowledges = new ArrayList<>();

    /**
     * 关联的技能
     */
    @ManyToMany
    @JoinTable(
        name = "rf_application_skills",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills = new ArrayList<>();

    /**
     * 关联的工作流
     */
    @ManyToMany
    @JoinTable(
        name = "rf_application_workflows",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "workflow_id")
    )
    private List<Workflow> workflows = new ArrayList<>();
}