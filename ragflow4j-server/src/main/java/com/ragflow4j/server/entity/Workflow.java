package com.ragflow4j.server.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 工作流实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rf_workflow")
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 工作流名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 工作流描述
     */
    private String description;

    /**
     * 工作流配置
     */
    @Column(columnDefinition = "TEXT")
    private String configuration;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 创建者ID
     */
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    /**
     * 更新者ID
     */
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    /**
     * 关联的应用
     */
    @ManyToMany(mappedBy = "workflows")
    private Set<Application> applications = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}