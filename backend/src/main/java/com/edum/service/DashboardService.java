package com.edum.service;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    
    /**
     * 获取统计数据
     */
    Map<String, Object> getStatistics();
    
    /**
     * 获取班级人数分布
     */
    List<Map<String, Object>> getClassDistribution();
    
    /**
     * 获取近7天出勤趋势
     */
    List<Map<String, Object>> getAttendanceTrend();
}
