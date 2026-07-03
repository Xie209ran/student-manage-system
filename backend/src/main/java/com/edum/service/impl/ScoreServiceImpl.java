package com.edum.service.impl;

import com.edum.entity.Score;
import com.edum.mapper.ScoreMapper;
import com.edum.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩服务实现类
 */
@Service
public class ScoreServiceImpl implements ScoreService {
    
    @Autowired
    private ScoreMapper scoreMapper;
    
    /**
     * 分页查询成绩列表
     */
    @Override
    public Map<String, Object> getPage(int pageNum, int pageSize, Long classId, Long studentId, String subject, String examName) {
        int offset = (pageNum - 1) * pageSize;
        List<Score> list = scoreMapper.selectPage(classId, studentId, subject, examName, offset, pageSize);
        long total = scoreMapper.countTotal(classId, studentId, subject, examName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 根据ID查询成绩详情
     */
    @Override
    public Score getById(Long id) {
        Score score = scoreMapper.selectById(id);
        if (score == null) {
            throw new RuntimeException("成绩记录不存在");
        }
        return score;
    }
    
    /**
     * 批量录入成绩
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInput(List<Score> scores, Long inputUserId) {
        if (scores == null || scores.isEmpty()) {
            throw new RuntimeException("成绩记录不能为空");
        }
        
        // 为每条记录设置录入人信息
        for (Score score : scores) {
            // 参数校验
            if (score.getStudentId() == null) {
                throw new RuntimeException("学生ID不能为空");
            }
            if (score.getClassId() == null) {
                throw new RuntimeException("班级ID不能为空");
            }
            if (score.getSubject() == null || score.getSubject().trim().isEmpty()) {
                throw new RuntimeException("科目不能为空");
            }
            if (score.getExamName() == null || score.getExamName().trim().isEmpty()) {
                throw new RuntimeException("考试名称不能为空");
            }
            if (score.getScore() == null) {
                throw new RuntimeException("成绩不能为空");
            }
            
            // 成绩范围校验
            if (score.getFullScore() != null && score.getScore().compareTo(score.getFullScore()) > 0) {
                throw new RuntimeException("成绩不能超过满分");
            }
            
            // 设置默认值
            if (score.getFullScore() == null) {
                score.setFullScore(new BigDecimal("100"));
            }
            if (score.getPassScore() == null) {
                score.setPassScore(new BigDecimal("60"));
            }
            
            // 设置录入人ID
            score.setInputUserId(inputUserId);
        }
        
        // 批量插入或更新（同一学生同一科目同一考试已存在则覆盖）
        scoreMapper.batchInsertOrUpdate(scores);
    }
    
    /**
     * 更新成绩记录
     */
    @Override
    public void update(Score score, Long updateUserId) {
        // 检查成绩记录是否存在
        Score existing = scoreMapper.selectById(score.getId());
        if (existing == null) {
            throw new RuntimeException("成绩记录不存在");
        }
        
        // 参数校验
        if (score.getScore() != null) {
            if (existing.getFullScore() != null && score.getScore().compareTo(existing.getFullScore()) > 0) {
                throw new RuntimeException("成绩不能超过满分");
            }
        }
        
        // 设置修改人ID
        score.setUpdateUserId(updateUserId);
        
        scoreMapper.update(score);
    }
    
    /**
     * 获取成绩统计（按班级）
     */
    @Override
    public Map<String, Object> getStatisticsByClass(Long classId, String subject, String examName) {
        if (classId == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        
        Map<String, Object> statistics = scoreMapper.getStatisticsByClass(classId, subject, examName);
        
        // 处理null值
        if (statistics == null) {
            statistics = new HashMap<>();
            statistics.put("totalCount", 0);
            statistics.put("avgScore", 0.0);
            statistics.put("maxScore", 0.0);
            statistics.put("minScore", 0.0);
            statistics.put("passCount", 0);
            statistics.put("passRate", 0.0);
            statistics.put("excellentCount", 0);
            statistics.put("excellentRate", 0.0);
        }
        
        return statistics;
    }
    
    /**
     * 获取分数段分布
     */
    @Override
    public List<Map<String, Object>> getScoreDistribution(Long classId, String subject, String examName) {
        if (classId == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        
        List<Map<String, Object>> distribution = scoreMapper.getScoreDistribution(classId, subject, examName);
        
        // 如果没有数据，返回空的分数段
        if (distribution == null || distribution.isEmpty()) {
            distribution = new java.util.ArrayList<>();
            String[] ranges = {"90-100", "80-89", "70-79", "60-69", "0-59"};
            for (String range : ranges) {
                Map<String, Object> map = new HashMap<>();
                map.put("scoreRange", range);
                map.put("count", 0);
                map.put("percentage", 0.0);
                distribution.add(map);
            }
        }
        
        return distribution;
    }
}
