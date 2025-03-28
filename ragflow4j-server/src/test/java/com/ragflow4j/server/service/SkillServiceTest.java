package com.ragflow4j.server.service;

import com.ragflow4j.server.dto.SkillDTO;
import com.ragflow4j.server.entity.Skill;
import com.ragflow4j.server.repository.SkillRepository;
import com.ragflow4j.server.service.impl.SkillServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillServiceImpl skillService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSkill() {
        // 准备测试数据
        SkillDTO dto = new SkillDTO();
        dto.setName("Test Skill");
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Test Skill");

        // 设置mock行为
        when(skillRepository.save(any(Skill.class))).thenReturn(skill);

        // 执行测试
        SkillDTO result = skillService.createSkill(dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(skill.getId(), result.getId());
        assertEquals(skill.getName(), result.getName());
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void testGetSkill() {
        // 准备测试数据
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setName("Test Skill");

        // 设置mock行为
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));

        // 执行测试
        SkillDTO result = skillService.getSkill(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(skill.getId(), result.getId());
        assertEquals(skill.getName(), result.getName());
        verify(skillRepository).findById(1L);
    }

    @Test
    void testGetSkillNotFound() {
        // 设置mock行为
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> skillService.getSkill(1L));
        verify(skillRepository).findById(1L);
    }

    @Test
    void testGetAllSkills() {
        // 准备测试数据
        List<Skill> skills = Arrays.asList(
                createSkill(1L, "Skill 1"),
                createSkill(2L, "Skill 2")
        );

        // 设置mock行为
        when(skillRepository.findAll()).thenReturn(skills);

        // 执行测试
        List<SkillDTO> result = skillService.getAllSkills();

        // 验证结果
        assertNotNull(result);
        assertEquals(skills.size(), result.size());
        verify(skillRepository).findAll();
    }

    @Test
    void testSearchSkillsByName() {
        // 准备测试数据
        List<Skill> skills = Arrays.asList(
                createSkill(1L, "Test Skill 1"),
                createSkill(2L, "Test Skill 2")
        );

        // 设置mock行为
        when(skillRepository.findByNameContainingIgnoreCase("Test")).thenReturn(skills);

        // 执行测试
        List<SkillDTO> result = skillService.searchSkillsByName("Test");

        // 验证结果
        assertNotNull(result);
        assertEquals(skills.size(), result.size());
        verify(skillRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void testUpdateSkill() {
        // 准备测试数据
        SkillDTO dto = new SkillDTO();
        dto.setName("Updated Skill");
        Skill existingSkill = createSkill(1L, "Original Skill");
        Skill updatedSkill = createSkill(1L, "Updated Skill");

        // 设置mock行为
        when(skillRepository.findById(1L)).thenReturn(Optional.of(existingSkill));
        when(skillRepository.save(any(Skill.class))).thenReturn(updatedSkill);

        // 执行测试
        SkillDTO result = skillService.updateSkill(1L, dto);

        // 验证结果
        assertNotNull(result);
        assertEquals(updatedSkill.getId(), result.getId());
        assertEquals(updatedSkill.getName(), result.getName());
        verify(skillRepository).findById(1L);
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void testUpdateSkillNotFound() {
        // 设置mock行为
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> skillService.updateSkill(1L, new SkillDTO()));
        verify(skillRepository).findById(1L);
    }

    @Test
    void testDeleteSkill() {
        // 准备测试数据
        Skill skill = createSkill(1L, "Test Skill");

        // 设置mock行为
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        doNothing().when(skillRepository).delete(skill);

        // 执行测试
        skillService.deleteSkill(1L);

        // 验证结果
        verify(skillRepository).findById(1L);
        verify(skillRepository).delete(skill);
    }

    @Test
    void testDeleteSkillNotFound() {
        // 设置mock行为
        when(skillRepository.findById(1L)).thenReturn(Optional.empty());

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> skillService.deleteSkill(1L));
        verify(skillRepository).findById(1L);
    }

    private Skill createSkill(Long id, String name) {
        Skill skill = new Skill();
        skill.setId(id);
        skill.setName(name);
        return skill;
    }
} 