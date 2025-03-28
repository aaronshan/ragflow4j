package com.ragflow4j.server.service.impl;

import com.ragflow4j.server.dto.ApplicationDTO;
import com.ragflow4j.server.entity.Application;
import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.entity.Skill;
import com.ragflow4j.server.entity.Workflow;
import com.ragflow4j.server.repository.ApplicationRepository;
import com.ragflow4j.server.repository.KnowledgeRepository;
import com.ragflow4j.server.repository.SkillRepository;
import com.ragflow4j.server.repository.WorkflowRepository;
import com.ragflow4j.server.service.ApplicationService;
import com.ragflow4j.server.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 应用管理服务实现类
 */
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SkillRepository skillRepository;
    private final WorkflowRepository workflowRepository;
    private final KnowledgeRepository knowledgeRepository;

    @Override
    @Transactional
    public Application createApplication(ApplicationDTO dto, Long userId) {
        Application application = new Application();
        BeanUtils.copyProperties(dto, application, "knowledgeBases", "skills", "workflows", "enableVoiceOutput", "enabled", "showReferences");
        application.setCreatedBy(userId);
        application.setUpdatedBy(userId);
        
        // 处理关联的知识库
        if (dto.getKnowledgeIds() != null) {
            dto.getKnowledgeIds().forEach(knowledgeBaseId -> {
                Knowledge knowledge = knowledgeRepository.findById(knowledgeBaseId)
                        .orElseThrow(() -> new ResourceNotFoundException("知识库不存在", knowledgeBaseId));
                application.getKnowledges().add(knowledge);
            });
        }

        // 处理关联的技能
        if (dto.getSkillIds() != null) {
            dto.getSkillIds().forEach(skillId -> {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new ResourceNotFoundException("技能不存在", skillId));
                application.getSkills().add(skill);
            });
        }

        // 处理关联的工作流
        if (dto.getWorkflowIds() != null) {
            dto.getWorkflowIds().forEach(workflowId -> {
                Workflow workflow = workflowRepository.findById(workflowId)
                        .orElseThrow(() -> new ResourceNotFoundException("工作流不存在", workflowId));
                application.getWorkflows().add(workflow);
            });
        }

        // 设置 enableVoiceOutput
        if (dto.getEnableVoiceOutput() != null) {
            application.setEnableVoiceOutput(dto.getEnableVoiceOutput());
        }

        // 设置 enabled
        if (dto.getEnabled() != null) {
            application.setEnabled(dto.getEnabled());
        }

        // 设置 showReferences
        if (dto.getShowReferences() != null) {
            application.setShowReferences(dto.getShowReferences());
        }

        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public Application updateApplication(Long id, ApplicationDTO dto, Long userId) {
        Application application = getApplication(id);
        BeanUtils.copyProperties(dto, application, "id", "createdAt", "createdBy", "knowledgeBases", "skills", "workflows", "enableVoiceOutput", "enabled", "showReferences");
        application.setUpdatedBy(userId);

        // 更新关联的知识库
        if (dto.getKnowledgeIds() != null) {
            application.getKnowledges().clear();
            dto.getKnowledgeIds().forEach(knowledgeBaseId -> {
                Knowledge knowledge = knowledgeRepository.findById(knowledgeBaseId)
                        .orElseThrow(() -> new ResourceNotFoundException("知识库不存在", knowledgeBaseId));
                application.getKnowledges().add(knowledge);
            });
        }

        // 更新关联的技能
        if (dto.getSkillIds() != null) {
            application.getSkills().clear();
            dto.getSkillIds().forEach(skillId -> {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new ResourceNotFoundException("技能不存在", skillId));
                application.getSkills().add(skill);
            });
        }

        // 更新关联的工作流
        if (dto.getWorkflowIds() != null) {
            application.getWorkflows().clear();
            dto.getWorkflowIds().forEach(workflowId -> {
                Workflow workflow = workflowRepository.findById(workflowId)
                        .orElseThrow(() -> new ResourceNotFoundException("工作流不存在", workflowId));
                application.getWorkflows().add(workflow);
            });
        }

        // 设置 enableVoiceOutput
        if (dto.getEnableVoiceOutput() != null) {
            application.setEnableVoiceOutput(dto.getEnableVoiceOutput());
        }

        // 设置 enabled
        if (dto.getEnabled() != null) {
            application.setEnabled(dto.getEnabled());
        }

        // 设置 showReferences
        if (dto.getShowReferences() != null) {
            application.setShowReferences(dto.getShowReferences());
        }

        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("应用不存在", id);
        }
        applicationRepository.deleteById(id);
    }

    @Override
    public Application getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("应用不存在", id));
    }

    @Override
    public Page<Application> listApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Application toggleApplicationStatus(Long id, boolean enabled, Long userId) {
        Application application = getApplication(id);
        application.setEnabled(enabled);
        application.setUpdatedBy(userId);
        return applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void addSkillToApplication(Long applicationId, Long skillId) {
        Application application = getApplication(applicationId);
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("技能不存在", skillId));
        application.getSkills().add(skill);
        applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void removeSkillFromApplication(Long applicationId, Long skillId) {
        Application application = getApplication(applicationId);
        application.getSkills().removeIf(skill -> skill.getId().equals(skillId));
        applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void addWorkflowToApplication(Long applicationId, Long workflowId) {
        Application application = getApplication(applicationId);
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("工作流不存在", workflowId));
        application.getWorkflows().add(workflow);
        applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void removeWorkflowFromApplication(Long applicationId, Long workflowId) {
        Application application = getApplication(applicationId);
        application.getWorkflows().removeIf(workflow -> workflow.getId().equals(workflowId));
        applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void addKnowledgeBaseToApplication(Long applicationId, Long knowledgeBaseId) {
        Application application = getApplication(applicationId);
        Knowledge knowledge = knowledgeRepository.findById(knowledgeBaseId)
                .orElseThrow(() -> new ResourceNotFoundException("知识库不存在", knowledgeBaseId));
        application.getKnowledges().add(knowledge);
        applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void removeKnowledgeBaseFromApplication(Long applicationId, Long knowledgeBaseId) {
        Application application = getApplication(applicationId);
        application.getKnowledges().removeIf(kb -> kb.getId().equals(knowledgeBaseId));
        applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void removeKnowledge(Long applicationId, Long knowledgeId) {
        Application application = getApplication(applicationId);
        application.getKnowledges().removeIf(knowledge -> knowledge.getId().equals(knowledgeId));
        applicationRepository.save(application);
    }

    @Override
    public List<Application> searchApplicationsByName(String keyword) {
        return applicationRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Application> findApplicationsByType(String type) {
        return applicationRepository.findByType(type);
    }

    @Override
    public Page<Application> getAllApplications(Pageable pageable) {
        return applicationRepository.findAll(pageable);
    }

    @Override
    public Set<Knowledge> getKnowledges(Long applicationId) {
        Application application = getApplication(applicationId);
        return new HashSet<>(application.getKnowledges());
    }

    @Override
    public Set<Skill> getSkills(Long applicationId) {
        Application application = getApplication(applicationId);
        return new HashSet<>(application.getSkills());
    }

    @Override
    public Set<Workflow> getWorkflows(Long applicationId) {
        Application application = getApplication(applicationId);
        return new HashSet<>(application.getWorkflows());
    }
}