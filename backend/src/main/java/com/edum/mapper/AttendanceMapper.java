package com.edum.mapper;

import com.edum.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤Mapper接口
 */
@Mapper
public interface AttendanceMapper {
    
    /**
     * 分页查询考勤记录
     */
    List<Attendance> selectPage(@Param("classId") Long classId,
                                 @Param("studentId") Long studentId,
                                 @Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate,
                                 @Param("offset") int offset,
                                 @Param("pageSize") int pageSize);
    
    /**
     * 统计总数
     */
    long countTotal(@Param("classId") Long classId,
                    @Param("studentId") Long studentId,
                    @Param("startDate") LocalDate startDate,
                    @Param("endDate") LocalDate endDate);
    
    /**
     * 根据ID查询考勤详情
     */
    Attendance selectById(@Param("id") Long id);
    
    /**
     * 插入考勤记录
     */
    int insert(Attendance attendance);
    
    /**
     * 更新考勤记录
     */
    int update(Attendance attendance);
    
    /**
     * 逻辑删除考勤记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查同一天同一学生是否已有考勤记录
     */
    Attendance checkExistsByDateAndStudent(@Param("attendanceDate") LocalDate attendanceDate,
                                            @Param("studentId") Long studentId);
    
    /**
     * 批量插入或更新考勤记录
     */
    int batchInsertOrUpdate(List<Attendance> attendances);
    
    /**
     * 获取考勤统计（按班级）
     */
    Map<String, Object> getStatisticsByClass(@Param("classId") Long classId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
    
    /**
     * 获取考勤统计（按学生）
     */
    Map<String, Object> getStatisticsByStudent(@Param("studentId") Long studentId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}
