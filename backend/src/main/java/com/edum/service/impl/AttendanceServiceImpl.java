package com.edum.service.impl;

import com.edum.entity.Attendance;
import com.edum.mapper.AttendanceMapper;
import com.edum.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务实现类
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {
    
    @Autowired
    private AttendanceMapper attendanceMapper;
    
    /**
     * 分页查询考勤记录
     */
    @Override
    public Map<String, Object> getPage(int pageNum, int pageSize, Long classId, Long studentId, LocalDate startDate, LocalDate endDate) {
        int offset = (pageNum - 1) * pageSize;
        List<Attendance> list = attendanceMapper.selectPage(classId, studentId, startDate, endDate, offset, pageSize);
        long total = attendanceMapper.countTotal(classId, studentId, startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 根据ID查询考勤详情
     */
    @Override
    public Attendance getById(Long id) {
        Attendance attendance = attendanceMapper.selectById(id);
        if (attendance == null) {
            throw new RuntimeException("考勤记录不存在");
        }
        return attendance;
    }
    
    /**
     * 批量打卡
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCheckIn(List<Attendance> attendances, Long checkInUserId) {
        if (attendances == null || attendances.isEmpty()) {
            throw new RuntimeException("打卡记录不能为空");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 为每条记录设置打卡人和打卡时间
        for (Attendance attendance : attendances) {
            // 参数校验
            if (attendance.getStudentId() == null) {
                throw new RuntimeException("学生ID不能为空");
            }
            if (attendance.getClassId() == null) {
                throw new RuntimeException("班级ID不能为空");
            }
            if (attendance.getAttendanceDate() == null) {
                throw new RuntimeException("考勤日期不能为空");
            }
            if (attendance.getStatus() == null) {
                throw new RuntimeException("考勤状态不能为空");
            }
            
            // 设置打卡人和打卡时间
            attendance.setCheckInUserId(checkInUserId);
            attendance.setCheckInTime(now);
        }
        
        // 批量插入或更新（同一天同一学生已存在则更新）
        attendanceMapper.batchInsertOrUpdate(attendances);
    }
    
    /**
     * 更新考勤记录
     */
    @Override
    public void update(Attendance attendance) {
        // 检查考勤记录是否存在
        Attendance existing = attendanceMapper.selectById(attendance.getId());
        if (existing == null) {
            throw new RuntimeException("考勤记录不存在");
        }
        
        // 参数校验
        if (attendance.getStatus() != null && (attendance.getStatus() < 0 || attendance.getStatus() > 4)) {
            throw new RuntimeException("考勤状态必须在0-4之间");
        }
        
        attendanceMapper.update(attendance);
    }
    
    /**
     * 获取考勤统计（按班级）
     */
    @Override
    public Map<String, Object> getStatisticsByClass(Long classId, LocalDate startDate, LocalDate endDate) {
        if (classId == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        
        Map<String, Object> statistics = attendanceMapper.getStatisticsByClass(classId, startDate, endDate);
        
        // 处理null值
        if (statistics == null) {
            statistics = new HashMap<>();
            statistics.put("totalCount", 0);
            statistics.put("presentCount", 0);
            statistics.put("absentCount", 0);
            statistics.put("lateCount", 0);
            statistics.put("earlyLeaveCount", 0);
            statistics.put("leaveCount", 0);
            statistics.put("attendanceRate", 0.0);
        }
        
        return statistics;
    }
    
    /**
     * 获取考勤统计（按学生）
     */
    @Override
    public Map<String, Object> getStatisticsByStudent(Long studentId, LocalDate startDate, LocalDate endDate) {
        if (studentId == null) {
            throw new RuntimeException("学生ID不能为空");
        }
        
        Map<String, Object> statistics = attendanceMapper.getStatisticsByStudent(studentId, startDate, endDate);
        
        // 处理null值
        if (statistics == null) {
            statistics = new HashMap<>();
            statistics.put("totalCount", 0);
            statistics.put("presentCount", 0);
            statistics.put("absentCount", 0);
            statistics.put("lateCount", 0);
            statistics.put("earlyLeaveCount", 0);
            statistics.put("leaveCount", 0);
            statistics.put("attendanceRate", 0.0);
        }
        
        return statistics;
    }
}
