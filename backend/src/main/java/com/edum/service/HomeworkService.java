package com.edum.service;

import com.edum.entity.Homework;
import com.edum.entity.HomeworkSubmission;

import java.util.List;
import java.util.Map;

/**
 * 作业服务接口
 */
public interface HomeworkService {
    
    /**
     * 发布作业
     */
    void publish(Homework homework);
    
    /**
     * 分页查询作业列表
     */
    Map<String, Object> getPage(int pageNum, int pageSize, Long classId, Long teacherId, String subject, Integer status);
    
    /**
     * 根据ID查询作业详情
     */
    Homework getById(Long id);
    
    /**
     * 删除作业
     */
    void delete(Long id);
    
    /**
     * 提交作业
     */
    void submit(Long homeworkId, HomeworkSubmission submission, Long studentId);
    
    /**
     * 批改作业
     */
    void grade(Long submissionId, HomeworkSubmission submission, Long graderId);
    
    /**
     * 批量批改作业
     */
    void batchGrade(List<HomeworkSubmission> submissions, Long graderId);
    
    /**
     * 获取作业统计信息
     */
    Map<String, Object> getStatistics(Long homeworkId);
}
