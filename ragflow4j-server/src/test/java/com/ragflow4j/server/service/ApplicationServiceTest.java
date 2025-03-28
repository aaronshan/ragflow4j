package com.ragflow4j.server.service;

import com.ragflow4j.server.dto.ApplicationDTO;
import com.ragflow4j.server.entity.Application;
import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.entity.Skill;
import com.ragflow4j.server.entity.Workflow;
import com.ragflow4j.server.repository.ApplicationRepository;
import com.ragflow4j.server.repository.KnowledgeRepository;
import com.ragflow4j.server.repository.SkillRepository;
import com.ragflow4j.server.repository.WorkflowRepository;
import com.ragflow4j.server.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private KnowledgeRepository knowledgeRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private WorkflowRepository workflowRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

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
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        // 执行测试
        Application result = applicationService.createApplication(dto, 1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(application.getId(), result.getId());
        assertEquals(application.getName(), result.getName());
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void testGetApplication() {
        // 准备测试数据
        Application application = new Application();
        application.setId(1L);
        application.setName("Test App");

        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        // 执行测试
        Application result = applicationService.getApplication(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(application.getId(), result.getId());
        assertEquals(application.getName(), result.getName());
        verify(applicationRepository).findById(1L);
    }

    @Test
    void testGetApplicationNotFound() {
        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> applicationService.getApplication(1L));
        verify(applicationRepository).findById(1L);
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
        when(applicationRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // 执行测试
        Page<Application> result = applicationService.getAllApplications(PageRequest.of(0, 10));

        // 验证结果
        assertNotNull(result);
        assertEquals(applications.size(), result.getContent().size());
        verify(applicationRepository).findAll(any(PageRequest.class));
    }

    @Test
    void testSearchApplicationsByName() {
        // 准备测试数据
        List<Application> applications = Arrays.asList(
                createApplication(1L, "Test App 1"),
                createApplication(2L, "Test App 2")
        );

        // 设置mock行为
        when(applicationRepository.findByNameContainingIgnoreCase("Test")).thenReturn(applications);

        // 执行测试
        List<Application> result = applicationService.searchApplicationsByName("Test");

        // 验证结果
        assertNotNull(result);
        assertEquals(applications.size(), result.size());
        verify(applicationRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void testAddKnowledgeBaseToApplication() {
        // 准备测试数据
        Application application = createApplication(1L, "Test App");
        Knowledge knowledge = createKnowledge(1L, "Test Knowledge");

        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(knowledgeRepository.findById(1L)).thenReturn(Optional.of(knowledge));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        // 执行测试
        applicationService.addKnowledgeBaseToApplication(1L, 1L);

        // 验证结果
        verify(applicationRepository).findById(1L);
        verify(knowledgeRepository).findById(1L);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void testAddSkillToApplication() {
        // 准备测试数据
        Application application = createApplication(1L, "Test App");
        Skill skill = createSkill(1L, "Test Skill");

        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        // 执行测试
        applicationService.addSkillToApplication(1L, 1L);

        // 验证结果
        verify(applicationRepository).findById(1L);
        verify(skillRepository).findById(1L);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void testAddWorkflowToApplication() {
        // 准备测试数据
        Application application = createApplication(1L, "Test App");
        Workflow workflow = createWorkflow(1L, "Test Workflow");

        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(workflowRepository.findById(1L)).thenReturn(Optional.of(workflow));
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        // 执行测试
        applicationService.addWorkflowToApplication(1L, 1L);

        // 验证结果
        verify(applicationRepository).findById(1L);
        verify(workflowRepository).findById(1L);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void testGetKnowledges() {
        // 准备测试数据
        Application application = createApplication(1L, "Test App");
        List<Knowledge> knowledges = Arrays.asList(
                createKnowledge(1L, "Knowledge 1"),
                createKnowledge(2L, "Knowledge 2")
        );
        application.setKnowledges(knowledges);

        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        // 执行测试
        Set<Knowledge> result = applicationService.getKnowledges(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(knowledges.size(), result.size());
        verify(applicationRepository).findById(1L);
    }

    @Test
    void testGetSkills() {
        // 准备测试数据
        Application application = createApplication(1L, "Test App");
        List<Skill> skills = Arrays.asList(
                createSkill(1L, "Skill 1"),
                createSkill(2L, "Skill 2")
        );
        application.setSkills(skills);

        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        // 执行测试
        Set<Skill> result = applicationService.getSkills(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(skills.size(), result.size());
        verify(applicationRepository).findById(1L);
    }

    @Test
    void testGetWorkflows() {
        // 准备测试数据
        Application application = createApplication(1L, "Test App");
        List<Workflow> workflows = Arrays.asList(
                createWorkflow(1L, "Workflow 1"),
                createWorkflow(2L, "Workflow 2")
        );
        application.setWorkflows(workflows);

        // 设置mock行为
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        // 执行测试
        Set<Workflow> result = applicationService.getWorkflows(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(workflows.size(), result.size());
        verify(applicationRepository).findById(1L);
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

    private Skill createSkill(Long id, String name) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName(name);
        return skill;
    }

    private Workflow createWorkflow(Long id, String name) {
        Workflow workflow = new Workflow();
        workflow.setId(id);
        workflow.setName(name);
        return workflow;
    }
} 