package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 课程表实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourseSchedule extends BaseEntity {
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 班级名称（关联查询）
     */
    private String className;
    
    /**
     * 教师ID
     */
    private Long teacherId;
    
    /**
     * 教师姓名（关联查询）
     */
    private String teacherName;
    
    /**
     * 科目
     */
    private String subject;
    
    /**
     * 星期（1-7，1表示周一）
     */
    private Integer dayOfWeek;
    
    /**
     * 节次（1-8，表示第几节课）
     */
    private Integer period;
    
    /**
     * 教室
     */
    private String classroom;
    
    /**
     * 开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endDate;
}
