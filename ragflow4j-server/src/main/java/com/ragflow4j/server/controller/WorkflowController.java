package com.ragflow4j.server.controller;

import com.ragflow4j.server.dto.WorkflowDTO;
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

/**
 * 工作流管理控制器
 * 提供工作流的RESTful API接口
 */
@RestController
@RequestMapping("/api/workflows")
@Api(tags = "工作流管理", description = "工作流管理接口")
public class WorkflowController {

    private final WorkflowService workflowService;

    @Autowired
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    @ApiOperation("创建新工作流")
    public ResponseEntity<WorkflowDTO> createWorkflow(
            @ApiParam("工作流信息") @Valid @RequestBody WorkflowDTO workflowDTO) {
        WorkflowDTO savedWorkflow = workflowService.createWorkflow(workflowDTO);
        return new ResponseEntity<>(savedWorkflow, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取工作流")
    public ResponseEntity<WorkflowDTO> getWorkflowById(
            @ApiParam("工作流ID") @PathVariable Long id) {
        try {
            WorkflowDTO workflow = workflowService.getWorkflow(id);
            return new ResponseEntity<>(workflow, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @ApiOperation("获取所有工作流")
    public ResponseEntity<List<WorkflowDTO>> getAllWorkflows() {
        List<WorkflowDTO> workflows = workflowService.getAllWorkflows();
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }

    @GetMapping("/search")
    @ApiOperation("根据名称关键词搜索工作流")
    public ResponseEntity<List<WorkflowDTO>> searchWorkflowsByName(
            @ApiParam("名称关键词") @RequestParam String keyword) {
        List<WorkflowDTO> workflows = workflowService.searchWorkflowsByName(keyword);
        return new ResponseEntity<>(workflows, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新工作流")
    public ResponseEntity<WorkflowDTO> updateWorkflow(
            @ApiParam("工作流ID") @PathVariable Long id,
            @ApiParam("更新的工作流信息") @Valid @RequestBody WorkflowDTO workflowDTO) {
        try {
            WorkflowDTO updatedWorkflow = workflowService.updateWorkflow(id, workflowDTO);
            return new ResponseEntity<>(updatedWorkflow, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除工作流")
    public ResponseEntity<Void> deleteWorkflow(
            @ApiParam("工作流ID") @PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}