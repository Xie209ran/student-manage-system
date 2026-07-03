package com.edum.service;

import com.edum.entity.Score;

import java.util.List;
import java.util.Map;

/**
 * 成绩服务接口
 */
public interface ScoreService {
    
    /**
     * 分页查询成绩列表
     */
    Map<String, Object> getPage(int pageNum, int pageSize, Long classId, Long studentId, String subject, String examName);
    
    /**
     * 根据ID查询成绩详情
     */
    Score getById(Long id);
    
    /**
     * 批量录入成绩
     */
    void batchInput(List<Score> scores, Long inputUserId);
    
    /**
     * 更新成绩记录
     */
    void update(Score score, Long updateUserId);
    
    /**
     * 获取成绩统计（按班级）
     */
    Map<String, Object> getStatisticsByClass(Long classId, String subject, String examName);
    
    /**
     * 获取分数段分布
     */
    List<Map<String, Object>> getScoreDistribution(Long classId, String subject, String examName);
}
