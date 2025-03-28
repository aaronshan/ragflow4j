package com.ragflow4j.server.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 文档实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id", nullable = false)
    private Knowledge knowledge;

    @Column(nullable = false)
    private String title;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(columnDefinition = "JSON")
    private String metadata;

    /**
     * 处理失败原因
     */
    @Column(columnDefinition = "TEXT")
    private String failureReason;

    /**
     * 文件MIME类型
     */
    private String mimeType;

    /**
     * 文档来源
     */
    private String source;

    /**
     * 文档内容
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 文档类型
     */
    @Column(nullable = false)
    private String documentType;

    /**
     * 向量ID，关联到向量存储中的ID
     */
    private String vectorId;

    /**
     * 文档状态枚举
     */
    public enum DocumentStatus {
        PROCESSING, COMPLETED, FAILED, DELETED
    }

    /**
     * 文档处理状态
     * UPLOADED: 已上传
     * PARSING: 解析中
     * PARSED: 已解析
     * SPLITTING: 切分中
     * SPLIT: 已切分
     * VECTORIZING: 向量化中
     * VECTORIZED: 已向量化
     * FAILED: 处理失败
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DocumentProcessStatus processStatus = DocumentProcessStatus.UPLOADED;
    

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = DocumentStatus.PROCESSING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}