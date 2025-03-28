package com.ragflow4j.server.service.impl;

import com.ragflow4j.server.dto.WorkflowDTO;
import com.ragflow4j.server.entity.Application;
import com.ragflow4j.server.entity.Workflow;
import com.ragflow4j.server.exception.ResourceNotFoundException;
import com.ragflow4j.server.repository.ApplicationRepository;
import com.ragflow4j.server.repository.WorkflowRepository;
import com.ragflow4j.server.service.WorkflowService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工作流管理服务实现类
 */
@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final ApplicationRepository applicationRepository;

    public WorkflowServiceImpl(WorkflowRepository workflowRepository, ApplicationRepository applicationRepository) {
        this.workflowRepository = workflowRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public WorkflowDTO createWorkflow(WorkflowDTO workflowDTO) {
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(workflowDTO, workflow);
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setUpdatedAt(LocalDateTime.now());
        workflow = workflowRepository.save(workflow);
        return convertToDTO(workflow);
    }

    @Override
    public WorkflowDTO updateWorkflow(Long id, WorkflowDTO workflowDTO) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));
        BeanUtils.copyProperties(workflowDTO, workflow, "id", "createdAt");
        workflow.setUpdatedAt(LocalDateTime.now());
        workflow = workflowRepository.save(workflow);
        return convertToDTO(workflow);
    }

    @Override
    public void deleteWorkflow(Long id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));
        workflowRepository.delete(workflow);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkflowDTO getWorkflow(Long id) {
        Workflow workflow = workflowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + id));
        return convertToDTO(workflow);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDTO> getAllWorkflows() {
        return workflowRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private WorkflowDTO convertToDTO(Workflow workflow) {
        WorkflowDTO workflowDTO = new WorkflowDTO();
        BeanUtils.copyProperties(workflow, workflowDTO);
        workflowDTO.setApplicationIds(workflow.getApplications().stream()
                .map(Application::getId)
                .collect(Collectors.toSet()));
        return workflowDTO;
    }

    @Override
    public void addApplicationToWorkflow(Long workflowId, Long applicationId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + workflowId));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        workflow.getApplications().add(application);
        workflowRepository.save(workflow);
    }

    @Override
    public void removeApplicationFromWorkflow(Long workflowId, Long applicationId) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow not found with id: " + workflowId));
        workflow.getApplications().removeIf(app -> app.getId().equals(applicationId));
        workflowRepository.save(workflow);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkflowDTO> searchWorkflowsByName(String keyword) {
        return workflowRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}