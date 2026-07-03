package com.edum.service.impl;

import com.edum.entity.CourseSchedule;
import com.edum.mapper.CourseMapper;
import com.edum.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 课程服务实现类
 */
@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseMapper courseMapper;
    
    /**
     * 查询班级课表
     */
    @Override
    public List<CourseSchedule> getClassSchedule(Long classId) {
        if (classId == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        
        return courseMapper.selectByClassId(classId);
    }
    
    /**
     * 根据ID查询课程详情
     */
    @Override
    public CourseSchedule getById(Long id) {
        CourseSchedule course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        return course;
    }
    
    /**
     * 添加课程
     */
    @Override
    public void add(CourseSchedule courseSchedule) {
        // 参数校验
        validateCourse(courseSchedule);
        
        // 检查时间冲突（同一班级同一时间段）
        CourseSchedule classConflict = courseMapper.checkClassTimeConflict(
                courseSchedule.getClassId(),
                courseSchedule.getDayOfWeek(),
                courseSchedule.getPeriod(),
                null
        );
        if (classConflict != null) {
            String dayName = getDayName(courseSchedule.getDayOfWeek());
            throw new RuntimeException("该班级在" + dayName + "第" + courseSchedule.getPeriod() + "节已有课程安排");
        }
        
        // 检查时间冲突（同一教师同一时间段）
        CourseSchedule teacherConflict = courseMapper.checkTeacherTimeConflict(
                courseSchedule.getTeacherId(),
                courseSchedule.getDayOfWeek(),
                courseSchedule.getPeriod(),
                null
        );
        if (teacherConflict != null) {
            String dayName = getDayName(courseSchedule.getDayOfWeek());
            throw new RuntimeException(teacherConflict.getTeacherName() + "老师在" + dayName + "第" + courseSchedule.getPeriod() + "节已有课程安排（" + teacherConflict.getClassName() + "）");
        }
        
        courseMapper.insert(courseSchedule);
    }
    
    /**
     * 更新课程
     */
    @Override
    public void update(CourseSchedule courseSchedule) {
        // 检查课程是否存在
        CourseSchedule existing = courseMapper.selectById(courseSchedule.getId());
        if (existing == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 参数校验
        validateCourse(courseSchedule);
        
        // 检查时间冲突（同一班级同一时间段，排除自身）
        CourseSchedule classConflict = courseMapper.checkClassTimeConflict(
                courseSchedule.getClassId(),
                courseSchedule.getDayOfWeek(),
                courseSchedule.getPeriod(),
                courseSchedule.getId()
        );
        if (classConflict != null) {
            String dayName = getDayName(courseSchedule.getDayOfWeek());
            throw new RuntimeException("该班级在" + dayName + "第" + courseSchedule.getPeriod() + "节已有课程安排");
        }
        
        // 检查时间冲突（同一教师同一时间段，排除自身）
        CourseSchedule teacherConflict = courseMapper.checkTeacherTimeConflict(
                courseSchedule.getTeacherId(),
                courseSchedule.getDayOfWeek(),
                courseSchedule.getPeriod(),
                courseSchedule.getId()
        );
        if (teacherConflict != null) {
            String dayName = getDayName(courseSchedule.getDayOfWeek());
            throw new RuntimeException(teacherConflict.getTeacherName() + "老师在" + dayName + "第" + courseSchedule.getPeriod() + "节已有课程安排（" + teacherConflict.getClassName() + "）");
        }
        
        courseMapper.update(courseSchedule);
    }
    
    /**
     * 删除课程
     */
    @Override
    public void delete(Long id) {
        // 检查课程是否存在
        CourseSchedule course = courseMapper.selectById(id);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 逻辑删除
        courseMapper.deleteById(id);
    }
    
    /**
     * 校验课程参数
     */
    private void validateCourse(CourseSchedule courseSchedule) {
        if (courseSchedule.getClassId() == null) {
            throw new RuntimeException("班级不能为空");
        }
        if (courseSchedule.getTeacherId() == null) {
            throw new RuntimeException("教师不能为空");
        }
        if (courseSchedule.getSubject() == null || courseSchedule.getSubject().trim().isEmpty()) {
            throw new RuntimeException("科目不能为空");
        }
        if (courseSchedule.getDayOfWeek() == null || courseSchedule.getDayOfWeek() < 1 || courseSchedule.getDayOfWeek() > 7) {
            throw new RuntimeException("星期必须在1-7之间");
        }
        if (courseSchedule.getPeriod() == null || courseSchedule.getPeriod() < 1 || courseSchedule.getPeriod() > 8) {
            throw new RuntimeException("节次必须在1-8之间");
        }
    }
    
    /**
     * 获取星期名称
     */
    private String getDayName(Integer dayOfWeek) {
        String[] days = {"", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        if (dayOfWeek >= 1 && dayOfWeek <= 7) {
            return days[dayOfWeek];
        }
        return "未知";
    }
}
