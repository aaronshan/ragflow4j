package com.ragflow4j.server.service;

import com.ragflow4j.server.dto.SkillDTO;
import java.util.List;

/**
 * 技能管理服务接口
 */
public interface SkillService {
    /**
     * 创建技能
     *
     * @param skillDTO 技能数据传输对象
     * @return 创建后的技能数据传输对象
     */
    SkillDTO createSkill(SkillDTO skillDTO);

    /**
     * 更新技能
     *
     * @param id 技能ID
     * @param skillDTO 技能数据传输对象
     * @return 更新后的技能数据传输对象
     */
    SkillDTO updateSkill(Long id, SkillDTO skillDTO);

    /**
     * 删除技能
     *
     * @param id 技能ID
     */
    void deleteSkill(Long id);

    /**
     * 获取技能详情
     *
     * @param id 技能ID
     * @return 技能数据传输对象
     */
    SkillDTO getSkill(Long id);

    /**
     * 获取所有技能
     *
     * @return 技能数据传输对象列表
     */
    List<SkillDTO> getAllSkills();

    /**
     * 根据名称关键词搜索技能
     *
     * @param keyword 名称关键词
     * @return 技能列表
     */
    List<SkillDTO> searchSkillsByName(String keyword);
}