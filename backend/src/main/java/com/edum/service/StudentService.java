package com.edum.service;

import com.edum.entity.Student;

import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 */
public interface StudentService {
    
    /**
     * 分页查询学生列表
     */
    Map<String, Object> getPage(int pageNum, int pageSize, String name, Long classId);
    
    /**
     * 根据ID查询学生详情
     */
    Student getById(Long id);
    
    /**
     * 新增学生
     */
    void add(Student student);
    
    /**
     * 更新学生
     */
    void update(Student student);
    
    /**
     * 删除学生
     */
    void delete(Long id);
    
    /**
     * 批量删除学生
     */
    void deleteBatch(List<Long> ids);
}
