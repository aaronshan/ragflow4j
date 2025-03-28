package com.ragflow4j.server.controller;

import com.ragflow4j.server.entity.Knowledge;
import com.ragflow4j.server.entity.User;
import com.ragflow4j.server.service.KnowledgeService;
import com.ragflow4j.server.service.UserService;
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
import java.util.Optional;
import java.util.UUID;

/**
 * 知识库管理控制器
 * 提供知识库元信息的RESTful API接口
 */
@RestController
@RequestMapping("/api/knowledges")
@Api(tags = "知识库管理", description = "知识库元信息管理接口")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final UserService userService;
    
    @Autowired
    public KnowledgeController(KnowledgeService knowledgeService, UserService userService) {
        this.knowledgeService = knowledgeService;
        this.userService = userService;
    }
    
    @PostMapping
    @ApiOperation("创建新知识库")
    public ResponseEntity<Knowledge> createKnowledge(
            @ApiParam("知识库信息") @Valid @RequestBody Knowledge knowledge,
            @ApiParam("所有者ID") @RequestParam UUID ownerId) {
        Optional<User> owner = userService.findById(ownerId);
        if (!owner.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        knowledge.setOwner(owner.get());
        Knowledge savedKnowledge = knowledgeService.saveKnowledge(knowledge);
        return new ResponseEntity<>(savedKnowledge, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取知识库")
    public ResponseEntity<Knowledge> getKnowledgeById(
            @ApiParam("知识库ID") @PathVariable Long id) {
        return knowledgeService.findKnowledgeById(id)
                .map(knowledge -> new ResponseEntity<>(knowledge, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping
    @ApiOperation("获取所有知识库（分页）")
    public ResponseEntity<Page<Knowledge>> getAllKnowledges(
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Knowledge> knowledgeBases = knowledgeService.getAllKnowledges(pageRequest);
        return new ResponseEntity<>(knowledgeBases, HttpStatus.OK);
    }
    
    @GetMapping("/type/{type}")
    @ApiOperation("根据类型获取知识库")
    public ResponseEntity<List<Knowledge>> getKnowledgesByType(
            @ApiParam("知识库类型") @PathVariable String type) {
        List<Knowledge> knowledgeBases = knowledgeService.findKnowledgesByType(type);
        return new ResponseEntity<>(knowledgeBases, HttpStatus.OK);
    }
    
    @GetMapping("/search")
    @ApiOperation("根据名称关键词搜索知识库")
    public ResponseEntity<List<Knowledge>> searchKnowledgesByName(
            @ApiParam("名称关键词") @RequestParam String keyword) {
        List<Knowledge> knowledgeBases = knowledgeService.searchKnowledgesByName(keyword);
        return new ResponseEntity<>(knowledgeBases, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    @ApiOperation("获取用户拥有的知识库")
    public ResponseEntity<List<Knowledge>> getKnowledgesByOwner(
            @ApiParam("用户ID") @PathVariable UUID userId) {
        Optional<User> user = userService.findById(userId);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Knowledge> knowledgeBases = knowledgeService.findKnowledgesByOwner(user.get());
        return new ResponseEntity<>(knowledgeBases, HttpStatus.OK);
    }
    
    @GetMapping("/accessible/{userId}")
    @ApiOperation("获取用户有权访问的知识库")
    public ResponseEntity<List<Knowledge>> getAccessibleKnowledges(
            @ApiParam("用户ID") @PathVariable UUID userId) {
        Optional<User> user = userService.findById(userId);
        if (!user.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Knowledge> knowledgeBases = knowledgeService.findKnowledgesByAuthorizedUser(user.get());
        return new ResponseEntity<>(knowledgeBases, HttpStatus.OK);
    }
    
    @PutMapping("/{id}")
    @ApiOperation("更新知识库")
    public ResponseEntity<Knowledge> updateKnowledge(
            @ApiParam("知识库ID") @PathVariable Long id,
            @ApiParam("更新的知识库信息") @Valid @RequestBody Knowledge knowledge) {
        try {
            Knowledge updatedKnowledge = knowledgeService.updateKnowledge(id, knowledge);
            return new ResponseEntity<>(updatedKnowledge, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation("删除知识库")
    public ResponseEntity<Void> deleteKnowledge(
            @ApiParam("知识库ID") @PathVariable Long id) {
        knowledgeService.deleteKnowledge(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PostMapping("/{knowledgeId}/authorize/{userId}")
    @ApiOperation("授权用户访问知识库")
    public ResponseEntity<Void> authorizeUser(
            @ApiParam("知识库ID") @PathVariable Long knowledgeId,
            @ApiParam("用户ID") @PathVariable UUID userId) {
        try {
            knowledgeService.authorizeUser(knowledgeId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @DeleteMapping("/{knowledgeId}/authorize/{userId}")
    @ApiOperation("撤销用户访问知识库的权限")
    public ResponseEntity<Void> revokeUserAuthorization(
            @ApiParam("知识库ID") @PathVariable Long knowledgeId,
            @ApiParam("用户ID") @PathVariable UUID userId) {
        try {
            knowledgeService.revokeUserAuthorization(knowledgeId, userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}