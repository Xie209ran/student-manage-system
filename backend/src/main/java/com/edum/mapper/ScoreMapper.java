package com.edum.mapper;

import com.edum.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 成绩Mapper接口
 */
@Mapper
public interface ScoreMapper {
    
    /**
     * 分页查询成绩列表
     */
    List<Score> selectPage(@Param("classId") Long classId,
                            @Param("studentId") Long studentId,
                            @Param("subject") String subject,
                            @Param("examName") String examName,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);
    
    /**
     * 统计总数
     */
    long countTotal(@Param("classId") Long classId,
                    @Param("studentId") Long studentId,
                    @Param("subject") String subject,
                    @Param("examName") String examName);
    
    /**
     * 根据ID查询成绩详情
     */
    Score selectById(@Param("id") Long id);
    
    /**
     * 插入成绩记录
     */
    int insert(Score score);
    
    /**
     * 更新成绩记录
     */
    int update(Score score);
    
    /**
     * 逻辑删除成绩记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查同一学生同一科目同一考试是否已有成绩记录
     */
    Score checkExistsByStudentSubjectExam(@Param("studentId") Long studentId,
                                           @Param("subject") String subject,
                                           @Param("examName") String examName);
    
    /**
     * 批量插入或更新成绩记录
     */
    int batchInsertOrUpdate(List<Score> scores);
    
    /**
     * 获取成绩统计（按班级）
     */
    Map<String, Object> getStatisticsByClass(@Param("classId") Long classId,
                                              @Param("subject") String subject,
                                              @Param("examName") String examName);
    
    /**
     * 获取成绩统计（按科目）
     */
    Map<String, Object> getStatisticsBySubject(@Param("classId") Long classId,
                                                @Param("subject") String subject,
                                                @Param("examName") String examName);
    
    /**
     * 获取分数段分布
     */
    List<Map<String, Object>> getScoreDistribution(@Param("classId") Long classId,
                                                     @Param("subject") String subject,
                                                     @Param("examName") String examName);
}
