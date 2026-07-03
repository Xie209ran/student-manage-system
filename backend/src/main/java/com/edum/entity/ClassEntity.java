package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 班级实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClassEntity extends BaseEntity {
    
    /**
     * 班级名称
     */
    private String className;
    
    /**
     * 年级（1-12）
     */
    private Integer grade;
    
    /**
     * 负责教师ID
     */
    private Long teacherId;
    
    /**
     * 负责教师姓名（关联查询）
     */
    private String teacherName;
    
    /**
     * 教室位置
     */
    private String classroom;
    
    /**
     * 最大人数
     */
    private Integer maxCapacity;
    
    /**
     * 学生人数（统计字段）
     */
    private Integer studentCount;
    
    /**
     * 成立日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate establishDate;
}
