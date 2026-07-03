package com.edum.mapper;

import com.edum.entity.Homework;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业Mapper接口
 */
@Mapper
public interface HomeworkMapper {
    
    /**
     * 分页查询作业列表
     */
    List<Homework> selectPage(@Param("classId") Long classId,
                               @Param("teacherId") Long teacherId,
                               @Param("subject") String subject,
                               @Param("status") Integer status,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);
    
    /**
     * 统计总数
     */
    long countTotal(@Param("classId") Long classId,
                    @Param("teacherId") Long teacherId,
                    @Param("subject") String subject,
                    @Param("status") Integer status);
    
    /**
     * 根据ID查询作业详情
     */
    Homework selectById(@Param("id") Long id);
    
    /**
     * 插入作业
     */
    int insert(Homework homework);
    
    /**
     * 更新作业
     */
    int update(Homework homework);
    
    /**
     * 逻辑删除作业
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查作业是否有提交记录
     */
    long countSubmissionsByHomeworkId(@Param("homeworkId") Long homeworkId);
}
