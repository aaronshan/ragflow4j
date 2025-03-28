package com.ragflow4j.server.service.impl;

import com.ragflow4j.server.dto.SkillDTO;
import com.ragflow4j.server.entity.Application;
import com.ragflow4j.server.entity.Skill;
import com.ragflow4j.server.exception.ResourceNotFoundException;
import com.ragflow4j.server.repository.SkillRepository;
import com.ragflow4j.server.service.SkillService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 技能管理服务实现类
 */
@Service
@Transactional
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Override
    public SkillDTO createSkill(SkillDTO skillDTO) {
        Skill skill = new Skill();
        BeanUtils.copyProperties(skillDTO, skill);
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());
        skill = skillRepository.save(skill);
        return convertToDTO(skill);
    }

    @Override
    public SkillDTO updateSkill(Long id, SkillDTO skillDTO) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        BeanUtils.copyProperties(skillDTO, skill, "id", "createdAt");
        skill.setUpdatedAt(LocalDateTime.now());
        skill = skillRepository.save(skill);
        return convertToDTO(skill);
    }

    @Override
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        skillRepository.delete(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public SkillDTO getSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        return convertToDTO(skill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> getAllSkills() {
        return skillRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDTO> searchSkillsByName(String keyword) {
        return skillRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SkillDTO convertToDTO(Skill skill) {
        SkillDTO skillDTO = new SkillDTO();
        BeanUtils.copyProperties(skill, skillDTO);
        skillDTO.setApplicationIds(skill.getApplications().stream()
                .map(Application::getId)
                .collect(Collectors.toSet()));
        return skillDTO;
    }
}