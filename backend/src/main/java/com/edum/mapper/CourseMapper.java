package com.edum.mapper;

import com.edum.entity.CourseSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程Mapper接口
 */
@Mapper
public interface CourseMapper {
    
    /**
     * 查询班级课表（按星期和节次排序）
     */
    List<CourseSchedule> selectByClassId(@Param("classId") Long classId);
    
    /**
     * 根据ID查询课程详情
     */
    CourseSchedule selectById(@Param("id") Long id);
    
    /**
     * 插入课程
     */
    int insert(CourseSchedule courseSchedule);
    
    /**
     * 更新课程
     */
    int update(CourseSchedule courseSchedule);
    
    /**
     * 逻辑删除课程
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查时间冲突（同一班级同一时间段）
     */
    CourseSchedule checkClassTimeConflict(@Param("classId") Long classId,
                                           @Param("dayOfWeek") Integer dayOfWeek,
                                           @Param("period") Integer period,
                                           @Param("excludeId") Long excludeId);
    
    /**
     * 检查时间冲突（同一教师同一时间段）
     */
    CourseSchedule checkTeacherTimeConflict(@Param("teacherId") Long teacherId,
                                             @Param("dayOfWeek") Integer dayOfWeek,
                                             @Param("period") Integer period,
                                             @Param("excludeId") Long excludeId);
}
