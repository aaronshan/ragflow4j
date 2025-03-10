package com.ragflow4j.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 知识库实体类
 * 表示一个知识库，包含多个文档，并与用户关联
 */
@Data
@Entity
@Table(name = "knowledge_bases")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Knowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    /**
     * 知识库名称
     */
    @Column(nullable = false)
    private String name;
    
    /**
     * 知识库描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;
    
    /**
     * 知识库类型
     */
    private String type;
    
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
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
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