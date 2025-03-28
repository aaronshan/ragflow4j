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
 * 知识库实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "knowledge_bases")
public class Knowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private KnowledgeBaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "embedding_model", nullable = false, length = 100)
    private String embeddingModel;

    @Column(name = "chunk_size", nullable = false)
    private Integer chunkSize;

    @Column(name = "chunk_overlap", nullable = false)
    private Integer chunkOverlap;

    @Column(columnDefinition = "JSON")
    private String metadata;

    /**
     * 知识库类型
     */
    private String type;

    /**
     * 知识库状态枚举
     */
    public enum KnowledgeBaseStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (chunkSize == null) {
            chunkSize = 1000;
        }
        if (chunkOverlap == null) {
            chunkOverlap = 200;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 知识库拥有者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

     /**
     * 有权访问该知识库的用户
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "knowledge_user_access",
        joinColumns = @JoinColumn(name = "knowledge_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> authorizedUsers = new HashSet<>();
    
    /**
     * 知识库中的文档
     */
    @OneToMany(mappedBy = "knowledge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Document> documents = new HashSet<>();
    

    /**
     * 添加授权用户
     * 
     * @param user 要授权的用户
     */
    public void addAuthorizedUser(User user) {
        authorizedUsers.add(user);
        user.getAccessibleKnowledgeBases().add(this);
    }
    
    /**
     * 移除授权用户
     * 
     * @param user 要移除授权的用户
     */
    public void removeAuthorizedUser(User user) {
        authorizedUsers.remove(user);
        user.getAccessibleKnowledgeBases().remove(this);
    }
    
    /**
     * 添加文档到知识库
     * 
     * @param document 要添加的文档
     */
    public void addDocument(Document document) {
        documents.add(document);
        document.setKnowledge(this);
    }
    
    /**
     * 从知识库移除文档
     * 
     * @param document 要移除的文档
     */
    public void removeDocument(Document document) {
        documents.remove(document);
        document.setKnowledge(null);
    }
}
