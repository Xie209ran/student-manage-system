package com.edum.service;

import com.edum.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务接口
 */
public interface AttendanceService {
    
    /**
     * 分页查询考勤记录
     */
    Map<String, Object> getPage(int pageNum, int pageSize, Long classId, Long studentId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 根据ID查询考勤详情
     */
    Attendance getById(Long id);
    
    /**
     * 批量打卡
     */
    void batchCheckIn(List<Attendance> attendances, Long checkInUserId);
    
    /**
     * 更新考勤记录
     */
    void update(Attendance attendance);
    
    /**
     * 获取考勤统计（按班级）
     */
    Map<String, Object> getStatisticsByClass(Long classId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 获取考勤统计（按学生）
     */
    Map<String, Object> getStatisticsByStudent(Long studentId, LocalDate startDate, LocalDate endDate);
}
