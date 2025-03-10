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
 * 用户实体类
 */
@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    /**
     * 用户名
     */
    @Column(nullable = false, unique = true)
    private String username;
    
    /**
     * 密码（加密存储）
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * 用户邮箱
     */
    @Column(unique = true)
    private String email;
    
    /**
     * 用户角色（ADMIN, USER等）
     */
    @Column(nullable = false)
    private String role;
    
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
     * 用户拥有的知识库
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Knowledge> ownedKnowledgeBases = new HashSet<>();
    
    /**
     * 用户有权访问的知识库
     */
    @ManyToMany(mappedBy = "authorizedUsers", fetch = FetchType.LAZY)
    private Set<Knowledge> accessibleKnowledgeBases = new HashSet<>();
    
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