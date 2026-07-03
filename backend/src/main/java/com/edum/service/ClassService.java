package com.edum.service;

import com.edum.entity.ClassEntity;

import java.util.List;
import java.util.Map;

/**
 * 班级服务接口
 */
public interface ClassService {
    
    /**
     * 分页查询班级列表
     */
    Map<String, Object> getPage(int pageNum, int pageSize, String className, Integer grade, Long teacherId);
    
    /**
     * 根据ID查询班级详情
     */
    ClassEntity getById(Long id);
    
    /**
     * 新增班级
     */
    void add(ClassEntity classEntity);
    
    /**
     * 更新班级
     */
    void update(ClassEntity classEntity);
    
    /**
     * 删除班级
     */
    void delete(Long id);
}
