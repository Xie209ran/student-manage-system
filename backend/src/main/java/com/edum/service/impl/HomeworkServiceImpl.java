package com.edum.service.impl;

import com.edum.entity.Homework;
import com.edum.entity.HomeworkSubmission;
import com.edum.mapper.HomeworkMapper;
import com.edum.mapper.HomeworkSubmissionMapper;
import com.edum.service.HomeworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业服务实现类
 */
@Service
public class HomeworkServiceImpl implements HomeworkService {
    
    @Autowired
    private HomeworkMapper homeworkMapper;
    
    @Autowired
    private HomeworkSubmissionMapper submissionMapper;
    
    /**
     * 发布作业
     */
    @Override
    public void publish(Homework homework) {
        // 参数校验
        if (homework.getTitle() == null || homework.getTitle().trim().isEmpty()) {
            throw new RuntimeException("作业标题不能为空");
        }
        if (homework.getClassId() == null) {
            throw new RuntimeException("班级ID不能为空");
        }
        if (homework.getSubject() == null || homework.getSubject().trim().isEmpty()) {
            throw new RuntimeException("科目不能为空");
        }
        if (homework.getDeadline() == null) {
            throw new RuntimeException("截止时间不能为空");
        }
        
        // 设置默认状态为进行中
        homework.setStatus(1);
        
        homeworkMapper.insert(homework);
    }
    
    /**
     * 分页查询作业列表
     */
    @Override
    public Map<String, Object> getPage(int pageNum, int pageSize, Long classId, Long teacherId, String subject, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Homework> list = homeworkMapper.selectPage(classId, teacherId, subject, status, offset, pageSize);
        long total = homeworkMapper.countTotal(classId, teacherId, subject, status);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 根据ID查询作业详情
     */
    @Override
    public Homework getById(Long id) {
        Homework homework = homeworkMapper.selectById(id);
        if (homework == null) {
            throw new RuntimeException("作业不存在");
        }
        return homework;
    }
    
    /**
     * 删除作业
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查作业是否存在
        Homework homework = homeworkMapper.selectById(id);
        if (homework == null) {
            throw new RuntimeException("作业不存在");
        }
        
        // 检查是否有提交记录
        long count = homeworkMapper.countSubmissionsByHomeworkId(id);
        if (count > 0) {
            throw new RuntimeException("该作业已有" + count + "个学生提交，无法删除");
        }
        
        homeworkMapper.deleteById(id);
    }
    
    /**
     * 提交作业
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long homeworkId, HomeworkSubmission submission, Long studentId) {
        // 检查作业是否存在
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            throw new RuntimeException("作业不存在");
        }
        
        // 参数校验
        if (submission.getSubmitContent() == null || submission.getSubmitContent().trim().isEmpty()) {
            throw new RuntimeException("提交内容不能为空");
        }
        
        // 检查是否已提交
        HomeworkSubmission existing = submissionMapper.checkExistsByHomeworkAndStudent(homeworkId, studentId);
        
        LocalDateTime now = LocalDateTime.now();
        
        // 判断是否迟交
        int isLate = now.isAfter(homework.getDeadline()) ? 1 : 0;
        
        if (existing != null) {
            // 在截止时间前可多次提交，只保留最后一次
            if (isLate == 0) {
                // 更新提交内容和时间
                existing.setSubmitContent(submission.getSubmitContent());
                existing.setSubmitTime(now);
                existing.setIsLate(isLate);
                
                // 重置批改状态为未批改
                existing.setGradeStatus(0);
                existing.setScore(null);
                existing.setComment(null);
                existing.setGraderId(null);
                existing.setGradeTime(null);
                
                submissionMapper.updateForGrade(existing);
            } else {
                throw new RuntimeException("作业已截止，无法再次提交");
            }
        } else {
            // 首次提交
            submission.setHomeworkId(homeworkId);
            submission.setStudentId(studentId);
            submission.setClassId(homework.getClassId());
            submission.setSubmitTime(now);
            submission.setIsLate(isLate);
            submission.setGradeStatus(0);
            
            submissionMapper.insert(submission);
        }
    }
    
    /**
     * 批改作业
     */
    @Override
    public void grade(Long submissionId, HomeworkSubmission submission, Long graderId) {
        // 检查提交记录是否存在
        HomeworkSubmission existing = submissionMapper.selectById(submissionId);
        if (existing == null) {
            throw new RuntimeException("提交记录不存在");
        }
        
        // 参数校验
        if (submission.getScore() == null) {
            throw new RuntimeException("成绩不能为空");
        }
        
        // 设置批改信息
        submission.setId(submissionId);
        submission.setGraderId(graderId);
        submission.setGradeTime(LocalDateTime.now());
        
        submissionMapper.updateForGrade(submission);
    }
    
    /**
     * 批量批改作业
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchGrade(List<HomeworkSubmission> submissions, Long graderId) {
        if (submissions == null || submissions.isEmpty()) {
            throw new RuntimeException("批改记录不能为空");
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 为每条记录设置批改信息
        for (HomeworkSubmission submission : submissions) {
            // 参数校验
            if (submission.getId() == null) {
                throw new RuntimeException("提交记录ID不能为空");
            }
            if (submission.getScore() == null) {
                throw new RuntimeException("成绩不能为空");
            }
            
            // 设置批改人和批改时间
            submission.setGraderId(graderId);
            submission.setGradeTime(now);
        }
        
        // 批量更新
        submissionMapper.batchUpdateForGrade(submissions);
    }
    
    /**
     * 获取作业统计信息
     */
    @Override
    public Map<String, Object> getStatistics(Long homeworkId) {
        if (homeworkId == null) {
            throw new RuntimeException("作业ID不能为空");
        }
        
        // 获取基本统计信息
        Map<String, Object> statistics = submissionMapper.getStatisticsByHomeworkId(homeworkId);
        
        // 处理null值
        if (statistics == null) {
            statistics = new HashMap<>();
            statistics.put("totalCount", 0);
            statistics.put("submittedCount", 0);
            statistics.put("notSubmittedCount", 0);
            statistics.put("completionRate", 0.0);
            statistics.put("onTimeCount", 0);
            statistics.put("onTimeRate", 0.0);
            statistics.put("avgScore", 0.0);
        }
        
        // 获取未提交学生名单
        List<Map<String, Object>> notSubmittedStudents = submissionMapper.getNotSubmittedStudents(homeworkId);
        statistics.put("notSubmittedStudents", notSubmittedStudents);
        
        return statistics;
    }
}
