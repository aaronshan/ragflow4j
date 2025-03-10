package com.ragflow4j.server.service.impl;

import com.ragflow4j.server.model.Knowledge;
import com.ragflow4j.server.model.User;
import com.ragflow4j.server.repository.KnowledgeRepository;
import com.ragflow4j.server.repository.UserRepository;
import com.ragflow4j.server.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 知识库管理服务实现类
 */
@Service
@Transactional
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KnowledgeRepository knowledgeRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public KnowledgeBaseServiceImpl(KnowledgeRepository knowledgeRepository, UserRepository userRepository) {
        this.knowledgeRepository = knowledgeRepository;
        this.userRepository = userRepository;
    }
    
    @Override
    public Knowledge saveKnowledgeBase(Knowledge knowledge) {
        return knowledgeRepository.save(knowledge);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Knowledge> findKnowledgeBaseById(UUID id) {
        return knowledgeRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Knowledge> findKnowledgeBasesByType(String type) {
        return knowledgeRepository.findByType(type);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Knowledge> searchKnowledgeBasesByName(String keyword) {
        return knowledgeRepository.findByNameContaining(keyword);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Knowledge> getAllKnowledgeBases(Pageable pageable) {
        return knowledgeRepository.findAll(pageable);
    }
    
    @Override
    public void deleteKnowledgeBase(UUID id) {
        knowledgeRepository.deleteById(id);
    }
    
    @Override
    public Knowledge updateKnowledgeBase(UUID id, Knowledge knowledge) {
        Optional<Knowledge> existingKnowledge = knowledgeRepository.findById(id);
        if (existingKnowledge.isPresent()) {
            Knowledge updatedKnowledge = existingKnowledge.get();
            updatedKnowledge.setName(knowledge.getName());
            updatedKnowledge.setDescription(knowledge.getDescription());
            updatedKnowledge.setType(knowledge.getType());
            return knowledgeRepository.save(updatedKnowledge);
        } else {
            throw new RuntimeException("Knowledge base not found with id: " + id);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Knowledge> findKnowledgeBasesByOwner(User owner) {
        return knowledgeRepository.findByOwner(owner);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Knowledge> findKnowledgeBasesByAuthorizedUser(User user) {
        return knowledgeRepository.findByAuthorizedUsersContaining(user);
    }
    
    @Override
    public void authorizeUser(UUID knowledgeBaseId, UUID userId) {
        Optional<Knowledge> knowledgeOpt = knowledgeRepository.findById(knowledgeBaseId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (knowledgeOpt.isPresent() && userOpt.isPresent()) {
            Knowledge knowledge = knowledgeOpt.get();
            User user = userOpt.get();
            knowledge.addAuthorizedUser(user);
            knowledgeRepository.save(knowledge);
        } else {
            throw new RuntimeException("Knowledge base or user not found");
        }
    }
    
    @Override
    public void revokeUserAuthorization(UUID knowledgeBaseId, UUID userId) {
        Optional<Knowledge> knowledgeOpt = knowledgeRepository.findById(knowledgeBaseId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (knowledgeOpt.isPresent() && userOpt.isPresent()) {
            Knowledge knowledge = knowledgeOpt.get();
            User user = userOpt.get();
            knowledge.removeAuthorizedUser(user);
            knowledgeRepository.save(knowledge);
        } else {
            throw new RuntimeException("Knowledge base or user not found");
        }
    }
}