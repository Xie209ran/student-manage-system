package com.edum.mapper;

import com.edum.entity.HomeworkSubmission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 作业提交Mapper接口
 */
@Mapper
public interface HomeworkSubmissionMapper {
    
    /**
     * 分页查询作业提交列表
     */
    List<HomeworkSubmission> selectPage(@Param("homeworkId") Long homeworkId,
                                         @Param("studentId") Long studentId,
                                         @Param("gradeStatus") Integer gradeStatus,
                                         @Param("offset") int offset,
                                         @Param("pageSize") int pageSize);
    
    /**
     * 统计总数
     */
    long countTotal(@Param("homeworkId") Long homeworkId,
                    @Param("studentId") Long studentId,
                    @Param("gradeStatus") Integer gradeStatus);
    
    /**
     * 根据ID查询提交详情
     */
    HomeworkSubmission selectById(@Param("id") Long id);
    
    /**
     * 检查同一学生对同一作业是否已有提交记录
     */
    HomeworkSubmission checkExistsByHomeworkAndStudent(@Param("homeworkId") Long homeworkId,
                                                        @Param("studentId") Long studentId);
    
    /**
     * 插入提交记录
     */
    int insert(HomeworkSubmission submission);
    
    /**
     * 更新提交记录（用于批改）
     */
    int updateForGrade(HomeworkSubmission submission);
    
    /**
     * 批量更新提交记录（用于批量批改）
     */
    int batchUpdateForGrade(List<HomeworkSubmission> submissions);
    
    /**
     * 获取作业统计信息
     */
    Map<String, Object> getStatisticsByHomeworkId(@Param("homeworkId") Long homeworkId);
    
    /**
     * 获取未提交学生名单
     */
    List<Map<String, Object>> getNotSubmittedStudents(@Param("homeworkId") Long homeworkId);
}
