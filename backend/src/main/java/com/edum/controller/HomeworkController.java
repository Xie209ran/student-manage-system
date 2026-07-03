package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.Homework;
import com.edum.entity.HomeworkSubmission;
import com.edum.service.HomeworkService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 作业控制器
 */
@RestController
@RequestMapping("/api/homeworks")
@Validated
public class HomeworkController {
    
    @Autowired
    private HomeworkService homeworkService;
    
    /**
     * 发布作业
     */
    @PostMapping
    public Result<Void> publish(@RequestBody @Validated Homework homework, HttpServletRequest request) {
        try {
            // 从Token中获取教师ID
            Long teacherId = (Long) request.getAttribute("userId");
            if (teacherId == null) {
                return Result.error("未登录");
            }
            
            homework.setTeacherId(teacherId);
            homeworkService.publish(homework);
            return Result.success("发布成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询作业列表
     */
    @GetMapping
    public Result<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> data = homeworkService.getPage(pageNum, pageSize, classId, teacherId, subject, status);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询作业详情
     */
    @GetMapping("/{id}")
    public Result<Homework> getById(@PathVariable Long id) {
        try {
            Homework homework = homeworkService.getById(id);
            return Result.success(homework);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除作业
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            homeworkService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 提交作业
     */
    @PostMapping("/{homeworkId}/submit")
    public Result<Void> submit(@PathVariable Long homeworkId, @RequestBody HomeworkSubmission submission, HttpServletRequest request) {
        try {
            // 从Token中获取学生ID
            Long studentId = (Long) request.getAttribute("userId");
            if (studentId == null) {
                return Result.error("未登录");
            }
            
            homeworkService.submit(homeworkId, submission, studentId);
            return Result.success("提交成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批改作业
     */
    @PutMapping("/submissions/{id}")
    public Result<Void> grade(@PathVariable Long id, @RequestBody HomeworkSubmission submission, HttpServletRequest request) {
        try {
            // 从Token中获取批改人ID
            Long graderId = (Long) request.getAttribute("userId");
            if (graderId == null) {
                return Result.error("未登录");
            }
            
            homeworkService.grade(id, submission, graderId);
            return Result.success("批改成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量批改作业
     */
    @PutMapping("/{homeworkId}/batch-grade")
    public Result<Void> batchGrade(@PathVariable Long homeworkId, @RequestBody List<HomeworkSubmission> submissions, HttpServletRequest request) {
        try {
            // 从Token中获取批改人ID
            Long graderId = (Long) request.getAttribute("userId");
            if (graderId == null) {
                return Result.error("未登录");
            }
            
            homeworkService.batchGrade(submissions, graderId);
            return Result.success("批量批改成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取作业统计信息
     */
    @GetMapping("/{id}/statistics")
    public Result<Map<String, Object>> getStatistics(@PathVariable Long id) {
        try {
            Map<String, Object> statistics = homeworkService.getStatistics(id);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
