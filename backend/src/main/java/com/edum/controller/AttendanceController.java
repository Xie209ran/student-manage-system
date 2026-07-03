package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.Attendance;
import com.edum.service.AttendanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤控制器
 */
@RestController
@RequestMapping("/api/attendance")
@Validated
public class AttendanceController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    /**
     * 分页查询考勤记录
     */
    @GetMapping
    public Result<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            Map<String, Object> data = attendanceService.getPage(pageNum, pageSize, classId, studentId, startDate, endDate);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询考勤详情
     */
    @GetMapping("/{id}")
    public Result<Attendance> getById(@PathVariable Long id) {
        try {
            Attendance attendance = attendanceService.getById(id);
            return Result.success(attendance);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量打卡
     */
    @PostMapping("/batch")
    public Result<Void> batchCheckIn(@RequestBody List<Attendance> attendances, HttpServletRequest request) {
        try {
            // 从Token中获取打卡人ID
            Long checkInUserId = (Long) request.getAttribute("userId");
            if (checkInUserId == null) {
                return Result.error("未登录");
            }
            
            attendanceService.batchCheckIn(attendances, checkInUserId);
            return Result.success("打卡成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新考勤记录
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated Attendance attendance) {
        try {
            attendance.setId(id);
            attendanceService.update(attendance);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取考勤统计（按班级）
     */
    @GetMapping("/statistics/class")
    public Result<Map<String, Object>> getStatisticsByClass(
            @RequestParam Long classId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            Map<String, Object> statistics = attendanceService.getStatisticsByClass(classId, startDate, endDate);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取考勤统计（按学生）
     */
    @GetMapping("/statistics/student")
    public Result<Map<String, Object>> getStatisticsByStudent(
            @RequestParam Long studentId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            Map<String, Object> statistics = attendanceService.getStatisticsByStudent(studentId, startDate, endDate);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
