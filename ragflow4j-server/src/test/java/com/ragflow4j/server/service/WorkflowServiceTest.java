package com.ragflow4j.server.service;

import com.ragflow4j.server.dto.WorkflowDTO;
import com.ragflow4j.server.entity.Workflow;
import com.ragflow4j.server.repository.WorkflowRepository;
import com.ragflow4j.server.service.impl.WorkflowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkflowServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;

    @InjectMocks
    private WorkflowServiceImpl workflowService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWorkflow() {
        // 准备测试数据
        WorkflowDTO dto = new WorkflowDTO();
        dto.setName("Test Workflow");
        Workflow workflow = new Workflow();
        workflow.setId(1L);
        workflow.setName("Test Workflow");

        // 设置mock行为
        when(workflowRepository.save(any(Workflow.class))).thenReturn(workflow);

        // 执行测试
        WorkflowDTO result = workflowService.createWorkflow(dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(workflow.getId(), result.getId());
        assertEquals(workflow.getName(), result.getName());
        verify(workflowRepository).save(any(Workflow.class));
    }

    @Test
    void testGetWorkflow() {
        // 准备测试数据
        Workflow workflow = new Workflow();
        workflow.setId(1L);
        workflow.setName("Test Workflow");

        // 设置mock行为
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(workflow));

        // 执行测试
        WorkflowDTO result = workflowService.getWorkflow(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(workflow.getId(), result.getId());
        assertEquals(workflow.getName(), result.getName());
        verify(workflowRepository).findById(1L);
    }

    @Test
    void testGetWorkflowNotFound() {
        // 设置mock行为
        when(workflowRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> workflowService.getWorkflow(1L));
        verify(workflowRepository).findById(1L);
    }

    @Test
    void testGetAllWorkflows() {
        // 准备测试数据
        List<Workflow> workflows = Arrays.asList(
                createWorkflow(1L, "Workflow 1"),
                createWorkflow(2L, "Workflow 2")
        );

        // 设置mock行为
        when(workflowRepository.findAll()).thenReturn(workflows);

        // 执行测试
        List<WorkflowDTO> result = workflowService.getAllWorkflows();

        // 验证结果
        assertNotNull(result);
        assertEquals(workflows.size(), result.size());
        verify(workflowRepository).findAll();
    }

    @Test
    void testSearchWorkflowsByName() {
        // 准备测试数据
        List<Workflow> workflows = Arrays.asList(
                createWorkflow(1L, "Test Workflow 1"),
                createWorkflow(2L, "Test Workflow 2")
        );

        // 设置mock行为
        when(workflowRepository.findByNameContainingIgnoreCase("Test")).thenReturn(workflows);

        // 执行测试
        List<WorkflowDTO> result = workflowService.searchWorkflowsByName("Test");

        // 验证结果
        assertNotNull(result);
        assertEquals(workflows.size(), result.size());
        verify(workflowRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void testUpdateWorkflow() {
        // 准备测试数据
        WorkflowDTO dto = new WorkflowDTO();
        dto.setName("Updated Workflow");
        Workflow existingWorkflow = createWorkflow(1L, "Original Workflow");
        Workflow updatedWorkflow = createWorkflow(1L, "Updated Workflow");

        // 设置mock行为
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(existingWorkflow));
        when(workflowRepository.save(any(Workflow.class))).thenReturn(updatedWorkflow);

        // 执行测试
        WorkflowDTO result = workflowService.updateWorkflow(1L, dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(updatedWorkflow.getId(), result.getId());
        assertEquals(updatedWorkflow.getName(), result.getName());
        verify(workflowRepository).findById(1L);
        verify(workflowRepository).save(any(Workflow.class));
    }

    @Test
    void testUpdateWorkflowNotFound() {
        // 设置mock行为
        when(workflowRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> workflowService.updateWorkflow(1L, new WorkflowDTO()));
        verify(workflowRepository).findById(1L);
    }

    @Test
    void testDeleteWorkflow() {
        // 准备测试数据
        Workflow workflow = createWorkflow(1L, "Test Workflow");

        // 设置mock行为
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(workflow));
        doNothing().when(workflowRepository).delete(workflow);

        // 执行测试
        workflowService.deleteWorkflow(1L);

        // 验证结果
        verify(workflowRepository).findById(1L);
        verify(workflowRepository).delete(workflow);
    }

    @Test
    void testDeleteWorkflowNotFound() {
        // 设置mock行为
        when(workflowRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> workflowService.deleteWorkflow(1L));
        verify(workflowRepository).findById(1L);
    }

    private Workflow createWorkflow(Long id, String name) {
        Workflow workflow = new Workflow();
        workflow.setId(id);
        workflow.setName(name);
        return workflow;
    }
} 