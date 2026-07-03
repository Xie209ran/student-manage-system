package com.edum.service;

import com.edum.entity.CourseSchedule;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService {
    
    /**
     * 查询班级课表
     */
    List<CourseSchedule> getClassSchedule(Long classId);
    
    /**
     * 根据ID查询课程详情
     */
    CourseSchedule getById(Long id);
    
    /**
     * 添加课程
     */
    void add(CourseSchedule courseSchedule);
    
    /**
     * 更新课程
     */
    void update(CourseSchedule courseSchedule);
    
    /**
     * 删除课程
     */
    void delete(Long id);
}
