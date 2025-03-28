package com.ragflow4j.server.repository;

import com.ragflow4j.server.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 应用管理数据访问层接口
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    /**
     * 根据名称关键词搜索应用（不区分大小写）
     *
     * @param name 名称关键词
     * @return 应用列表
     */
    List<Application> findByNameContainingIgnoreCase(String name);

    /**
     * 根据类型查找应用
     *
     * @param type 应用类型
     * @return 应用列表
     */
    List<Application> findByType(String type);
}