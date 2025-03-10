package com.ragflow4j.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文档实体类，表示知识库中的一个文档
 */
@Data
@Entity
@Table(name = "documents")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    /**
     * 文档标题
     */
    @Column(nullable = false)
    private String title;
    
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
     * 文档来源
     */
    private String source;
    
    /**
     * 文档元数据，JSON格式
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * 向量ID，关联到向量存储中的ID
     */
    private String vectorId;
    
    /**
     * 所属知识库
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id")
    private Knowledge knowledge;
    
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
    
    /**
     * 处理失败原因
     */
    @Column(columnDefinition = "TEXT")
    private String failureReason;
    
    /**
     * 原始文件路径
     */
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件MIME类型
     */
    private String mimeType;
    
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