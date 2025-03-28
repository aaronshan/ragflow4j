package com.ragflow4j.server.repository;

import com.ragflow4j.server.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 技能数据访问接口
 */
@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    /**
     * 根据名称关键词搜索技能（不区分大小写）
     *
     * @param name 名称关键词
     * @return 技能列表
     */
    List<Skill> findByNameContainingIgnoreCase(String name);
}