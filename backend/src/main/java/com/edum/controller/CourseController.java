package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.CourseSchedule;
import com.edum.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 */
@RestController
@RequestMapping("/api/courses")
@Validated
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    /**
     * 查询班级课表
     */
    @GetMapping("/schedule")
    public Result<List<CourseSchedule>> getClassSchedule(@RequestParam Long classId) {
        try {
            List<CourseSchedule> schedule = courseService.getClassSchedule(classId);
            return Result.success(schedule);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询课程详情
     */
    @GetMapping("/{id}")
    public Result<CourseSchedule> getById(@PathVariable Long id) {
        try {
            CourseSchedule course = courseService.getById(id);
            return Result.success(course);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 添加课程
     */
    @PostMapping("/schedule")
    public Result<Void> add(@RequestBody @Validated CourseSchedule courseSchedule) {
        try {
            courseService.add(courseSchedule);
            return Result.success("添加成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新课程
     */
    @PutMapping("/schedule/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated CourseSchedule courseSchedule) {
        try {
            courseSchedule.setId(id);
            courseService.update(courseSchedule);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除课程
     */
    @DeleteMapping("/schedule/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            courseService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
