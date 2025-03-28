package com.ragflow4j.server.controller;

import com.ragflow4j.server.dto.SkillDTO;
import com.ragflow4j.server.service.SkillService;
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
 * 技能管理控制器
 * 提供技能的RESTful API接口
 */
@RestController
@RequestMapping("/api/skills")
@Api(tags = "技能管理", description = "技能管理接口")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    @ApiOperation("创建新技能")
    public ResponseEntity<SkillDTO> createSkill(
            @ApiParam("技能信息") @Valid @RequestBody SkillDTO skillDTO) {
        SkillDTO savedSkill = skillService.createSkill(skillDTO);
        return new ResponseEntity<>(savedSkill, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取技能")
    public ResponseEntity<SkillDTO> getSkillById(
            @ApiParam("技能ID") @PathVariable Long id) {
        try {
            SkillDTO skill = skillService.getSkill(id);
            return new ResponseEntity<>(skill, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @ApiOperation("获取所有技能")
    public ResponseEntity<List<SkillDTO>> getAllSkills() {
        List<SkillDTO> skills = skillService.getAllSkills();
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @GetMapping("/search")
    @ApiOperation("根据名称关键词搜索技能")
    public ResponseEntity<List<SkillDTO>> searchSkillsByName(
            @ApiParam("名称关键词") @RequestParam String keyword) {
        List<SkillDTO> skills = skillService.searchSkillsByName(keyword);
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新技能")
    public ResponseEntity<SkillDTO> updateSkill(
            @ApiParam("技能ID") @PathVariable Long id,
            @ApiParam("更新的技能信息") @Valid @RequestBody SkillDTO skillDTO) {
        try {
            SkillDTO updatedSkill = skillService.updateSkill(id, skillDTO);
            return new ResponseEntity<>(updatedSkill, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除技能")
    public ResponseEntity<Void> deleteSkill(
            @ApiParam("技能ID") @PathVariable Long id) {
        skillService.deleteSkill(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}