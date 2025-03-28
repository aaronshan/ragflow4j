package com.ragflow4j.server.controller;

import com.ragflow4j.server.dto.ApplicationDTO;
import com.ragflow4j.server.entity.Application;
import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.entity.Skill;
import com.ragflow4j.server.entity.Workflow;
import com.ragflow4j.server.service.ApplicationService;
import com.ragflow4j.server.service.KnowledgeService;
import com.ragflow4j.server.service.SkillService;
import com.ragflow4j.server.service.WorkflowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @Mock
    private KnowledgeService knowledgeService;

    @Mock
    private SkillService skillService;

    @Mock
    private WorkflowService workflowService;

    @InjectMocks
    private ApplicationController applicationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateApplication() {
        // 准备测试数据
        ApplicationDTO dto = new ApplicationDTO();
        dto.setName("Test App");
        Application application = new Application();
        application.setId(1L);
        application.setName("Test App");

        // 设置mock行为
        when(applicationService.createApplication(any(ApplicationDTO.class), anyLong()))
                .thenReturn(application);

        // 执行测试
        ResponseEntity<Application> response = applicationController.createApplication(dto, 1L);

        // 验证结果
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(application, response.getBody());
        verify(applicationService).createApplication(dto, 1L);
    }

    @Test
    void testGetApplicationById() {
        // 准备测试数据
        Application application = new Application();
        application.setId(1L);
        application.setName("Test App");

        // 设置mock行为
        when(applicationService.getApplication(1L)).thenReturn(application);

        // 执行测试
        ResponseEntity<Application> response = applicationController.getApplicationById(1L);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(application, response.getBody());
        verify(applicationService).getApplication(1L);
    }

    @Test
    void testGetApplicationByIdNotFound() {
        // 设置mock行为
        when(applicationService.getApplication(1L)).thenThrow(new RuntimeException("Not found"));

        // 执行测试
        ResponseEntity<Application> response = applicationController.getApplicationById(1L);

        // 验证结果
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllApplications() {
        // 准备测试数据
        List<Application> applications = Arrays.asList(
                createApplication(1L, "App 1"),
                createApplication(2L, "App 2")
        );
        Page<Application> page = new PageImpl<>(applications, PageRequest.of(0, 10), applications.size());

        // 设置mock行为
        when(applicationService.getAllApplications(any(PageRequest.class))).thenReturn(page);

        // 执行测试
        ResponseEntity<Page<Application>> response = applicationController.getAllApplications(0, 10);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(applicationService).getAllApplications(any(PageRequest.class));
    }

    @Test
    void testSearchApplicationsByName() {
        // 准备测试数据
        List<Application> applications = Arrays.asList(
                createApplication(1L, "Test App 1"),
                createApplication(2L, "Test App 2")
        );

        // 设置mock行为
        when(applicationService.searchApplicationsByName("Test")).thenReturn(applications);

        // 执行测试
        ResponseEntity<List<Application>> response = applicationController.searchApplicationsByName("Test");

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applications, response.getBody());
        verify(applicationService).searchApplicationsByName("Test");
    }

    @Test
    void testAddKnowledge() {
        // 设置mock行为
        doNothing().when(applicationService).addKnowledgeBaseToApplication(1L, 1L);

        // 执行测试
        ResponseEntity<Void> response = applicationController.addKnowledge(1L, 1L);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(applicationService).addKnowledgeBaseToApplication(1L, 1L);
    }

    @Test
    void testAddKnowledgeError() {
        // 设置mock行为
        doThrow(new RuntimeException("Error")).when(applicationService).addKnowledgeBaseToApplication(1L, 1L);

        // 执行测试
        ResponseEntity<Void> response = applicationController.addKnowledge(1L, 1L);

        // 验证结果
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(applicationService).addKnowledgeBaseToApplication(1L, 1L);
    }

    @Test
    void testGetKnowledges() {
        // 准备测试数据
        Set<Knowledge> knowledges = new HashSet<>(Arrays.asList(
                createKnowledge(1L, "Knowledge 1"),
                createKnowledge(2L, "Knowledge 2")
        ));

        // 设置mock行为
        when(applicationService.getKnowledges(1L)).thenReturn(knowledges);

        // 执行测试
        ResponseEntity<Set<Knowledge>> response = applicationController.getKnowledges(1L);

        // 验证结果
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(knowledges, response.getBody());
        verify(applicationService).getKnowledges(1L);
    }

    private Application createApplication(Long id, String name) {
        Application application = new Application();
        application.setId(id);
        application.setName(name);
        return application;
    }

    private Knowledge createKnowledge(Long id, String name) {
        Knowledge knowledge = new Knowledge();
        knowledge.setId(id);
        knowledge.setName(name);
        return knowledge;
    }
} 