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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 应用管理控制器
 * 提供应用的RESTful API接口
 */
@RestController
@RequestMapping("/api/applications")
@Api(tags = "应用管理", description = "应用管理接口")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final KnowledgeService knowledgeService;
    private final SkillService skillService;
    private final WorkflowService workflowService;

    @Autowired
    public ApplicationController(ApplicationService applicationService,
                               KnowledgeService knowledgeService,
                               SkillService skillService,
                               WorkflowService workflowService) {
        this.applicationService = applicationService;
        this.knowledgeService = knowledgeService;
        this.skillService = skillService;
        this.workflowService = workflowService;
    }

    @PostMapping
    @ApiOperation("创建新应用")
    public ResponseEntity<Application> createApplication(
            @ApiParam("应用信息") @Valid @RequestBody ApplicationDTO dto,
            @ApiParam("创建者ID") @RequestParam Long userId) {
        Application savedApplication = applicationService.createApplication(dto, userId);
        return new ResponseEntity<>(savedApplication, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取应用")
    public ResponseEntity<Application> getApplicationById(
            @ApiParam("应用ID") @PathVariable Long id) {
        try {
            Application application = applicationService.getApplication(id);
            return new ResponseEntity<>(application, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @ApiOperation("获取所有应用（分页）")
    public ResponseEntity<Page<Application>> getAllApplications(
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Application> applications = applicationService.getAllApplications(pageRequest);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/type/{type}")
    @ApiOperation("根据类型获取应用")
    public ResponseEntity<List<Application>> getApplicationsByType(
            @ApiParam("应用类型") @PathVariable String type) {
        List<Application> applications = applicationService.findApplicationsByType(type);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @GetMapping("/search")
    @ApiOperation("根据名称关键词搜索应用")
    public ResponseEntity<List<Application>> searchApplicationsByName(
            @ApiParam("名称关键词") @RequestParam String keyword) {
        List<Application> applications = applicationService.searchApplicationsByName(keyword);
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新应用")
    public ResponseEntity<Application> updateApplication(
            @ApiParam("应用ID") @PathVariable Long id,
            @ApiParam("更新的应用信息") @Valid @RequestBody ApplicationDTO dto,
            @ApiParam("更新者ID") @RequestParam Long userId) {
        try {
            Application updatedApplication = applicationService.updateApplication(id, dto, userId);
            return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除应用")
    public ResponseEntity<Void> deleteApplication(
            @ApiParam("应用ID") @PathVariable Long id) {
        applicationService.deleteApplication(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{applicationId}/knowledges/{knowledgeId}")
    @ApiOperation("关联知识库")
    public ResponseEntity<Void> addKnowledge(
            @ApiParam("应用ID") @PathVariable Long applicationId,
            @ApiParam("知识库ID") @PathVariable Long knowledgeId) {
        try {
            applicationService.addKnowledgeBaseToApplication(applicationId, knowledgeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{applicationId}/knowledges/{knowledgeId}")
    @ApiOperation("取消关联知识库")
    public ResponseEntity<Void> removeKnowledge(
            @ApiParam("应用ID") @PathVariable Long applicationId,
            @ApiParam("知识库ID") @PathVariable Long knowledgeId) {
        try {
            applicationService.removeKnowledgeBaseFromApplication(applicationId, knowledgeId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{applicationId}/skills/{skillId}")
    @ApiOperation("关联技能")
    public ResponseEntity<Void> addSkill(
            @ApiParam("应用ID") @PathVariable Long applicationId,
            @ApiParam("技能ID") @PathVariable Long skillId) {
        try {
            applicationService.addSkillToApplication(applicationId, skillId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{applicationId}/skills/{skillId}")
    @ApiOperation("取消关联技能")
    public ResponseEntity<Void> removeSkill(
            @ApiParam("应用ID") @PathVariable Long applicationId,
            @ApiParam("技能ID") @PathVariable Long skillId) {
        try {
            applicationService.removeSkillFromApplication(applicationId, skillId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{applicationId}/workflows/{workflowId}")
    @ApiOperation("关联工作流")
    public ResponseEntity<Void> addWorkflow(
            @ApiParam("应用ID") @PathVariable Long applicationId,
            @ApiParam("工作流ID") @PathVariable Long workflowId) {
        try {
            applicationService.addWorkflowToApplication(applicationId, workflowId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{applicationId}/workflows/{workflowId}")
    @ApiOperation("取消关联工作流")
    public ResponseEntity<Void> removeWorkflow(
            @ApiParam("应用ID") @PathVariable Long applicationId,
            @ApiParam("工作流ID") @PathVariable Long workflowId) {
        try {
            applicationService.removeWorkflowFromApplication(applicationId, workflowId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{applicationId}/knowledges")
    @ApiOperation("获取应用关联的所有知识库")
    public ResponseEntity<Set<Knowledge>> getKnowledges(
            @ApiParam("应用ID") @PathVariable Long applicationId) {
        try {
            Set<Knowledge> knowledges = applicationService.getKnowledges(applicationId);
            return new ResponseEntity<>(knowledges, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{applicationId}/skills")
    @ApiOperation("获取应用关联的所有技能")
    public ResponseEntity<Set<Skill>> getSkills(
            @ApiParam("应用ID") @PathVariable Long applicationId) {
        try {
            Set<Skill> skills = applicationService.getSkills(applicationId);
            return new ResponseEntity<>(skills, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{applicationId}/workflows")
    @ApiOperation("获取应用关联的所有工作流")
    public ResponseEntity<Set<Workflow>> getWorkflows(
            @ApiParam("应用ID") @PathVariable Long applicationId) {
        try {
            Set<Workflow> workflows = applicationService.getWorkflows(applicationId);
            return new ResponseEntity<>(workflows, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}