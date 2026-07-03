package com.edum.controller;

import com.edum.common.Result;
import com.edum.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘控制器
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> statistics = dashboardService.getStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取班级人数分布
     */
    @GetMapping("/class-distribution")
    public Result<List<Map<String, Object>>> getClassDistribution() {
        try {
            List<Map<String, Object>> distribution = dashboardService.getClassDistribution();
            return Result.success(distribution);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取近7天出勤趋势
     */
    @GetMapping("/attendance-trend")
    public Result<List<Map<String, Object>>> getAttendanceTrend() {
        try {
            List<Map<String, Object>> trend = dashboardService.getAttendanceTrend();
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
