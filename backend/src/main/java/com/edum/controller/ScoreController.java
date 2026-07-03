package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.Score;
import com.edum.service.ScoreService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 成绩控制器
 */
@RestController
@RequestMapping("/api/scores")
@Validated
public class ScoreController {
    
    @Autowired
    private ScoreService scoreService;
    
    /**
     * 分页查询成绩列表
     */
    @GetMapping
    public Result<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String examName) {
        try {
            Map<String, Object> data = scoreService.getPage(pageNum, pageSize, classId, studentId, subject, examName);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询成绩详情
     */
    @GetMapping("/{id}")
    public Result<Score> getById(@PathVariable Long id) {
        try {
            Score score = scoreService.getById(id);
            return Result.success(score);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量录入成绩
     */
    @PostMapping("/batch")
    public Result<Void> batchInput(@RequestBody List<Score> scores, HttpServletRequest request) {
        try {
            // 从Token中获取录入人ID
            Long inputUserId = (Long) request.getAttribute("userId");
            if (inputUserId == null) {
                return Result.error("未登录");
            }
            
            scoreService.batchInput(scores, inputUserId);
            return Result.success("录入成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新成绩记录
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated Score score, HttpServletRequest request) {
        try {
            // 从Token中获取修改人ID
            Long updateUserId = (Long) request.getAttribute("userId");
            if (updateUserId == null) {
                return Result.error("未登录");
            }
            
            score.setId(id);
            scoreService.update(score, updateUserId);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取成绩统计（按班级）
     */
    @GetMapping("/statistics/class")
    public Result<Map<String, Object>> getStatisticsByClass(
            @RequestParam Long classId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String examName) {
        try {
            Map<String, Object> statistics = scoreService.getStatisticsByClass(classId, subject, examName);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取分数段分布
     */
    @GetMapping("/statistics/distribution")
    public Result<List<Map<String, Object>>> getScoreDistribution(
            @RequestParam Long classId,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String examName) {
        try {
            List<Map<String, Object>> distribution = scoreService.getScoreDistribution(classId, subject, examName);
            return Result.success(distribution);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
