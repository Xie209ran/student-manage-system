package com.edum.service.impl;

import com.edum.mapper.AttendanceMapper;
import com.edum.mapper.ClassMapper;
import com.edum.mapper.HomeworkSubmissionMapper;
import com.edum.mapper.StudentMapper;
import com.edum.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private AttendanceMapper attendanceMapper;
    
    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;
    
    /**
     * 获取统计数据
     */
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 学生总数
        long totalStudents = studentMapper.countTotal(null, null);
        statistics.put("totalStudents", totalStudents);
        
        // 班级总数
        long totalClasses = classMapper.countTotal(null, null, null);
        statistics.put("totalClasses", totalClasses);
        
        // 今日出勤率
        LocalDate today = LocalDate.now();
        Map<String, Object> todayAttendance = attendanceMapper.getStatisticsByClass(null, today, today);
        if (todayAttendance != null && todayAttendance.get("attendanceRate") != null) {
            statistics.put("todayAttendanceRate", todayAttendance.get("attendanceRate"));
        } else {
            statistics.put("todayAttendanceRate", 0.0);
        }
        
        // 待批改作业数（未批改的提交记录）
        // 注意：这里简化处理，实际需要查询所有未批改的作业提交
        statistics.put("pendingHomeworks", 0); // TODO: 实现待批改作业统计
        
        return statistics;
    }
    
    /**
     * 获取班级人数分布
     */
    @Override
    public List<Map<String, Object>> getClassDistribution() {
        // 查询所有班级及其学生人数
        List<Map<String, Object>> distribution = classMapper.selectClassWithStudentCount();
        
        // 按学生数降序排列
        distribution.sort((a, b) -> {
            int countA = ((Number) a.get("studentCount")).intValue();
            int countB = ((Number) b.get("studentCount")).intValue();
            return Integer.compare(countB, countA); // 降序
        });
        
        return distribution;
    }
    
    /**
     * 获取近7天出勤趋势
     */
    @Override
    public List<Map<String, Object>> getAttendanceTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // 计算最近7天的出勤率
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            
            // 获取该天的统计数据
            Map<String, Object> dayStats = attendanceMapper.getStatisticsByClass(null, date, date);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());
            
            if (dayStats != null && dayStats.get("attendanceRate") != null) {
                dayData.put("attendanceRate", dayStats.get("attendanceRate"));
            } else {
                dayData.put("attendanceRate", 0.0);
            }
            
            trend.add(dayData);
        }
        
        return trend;
    }
}
