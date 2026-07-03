package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 作业提交实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HomeworkSubmission extends BaseEntity {
    
    /**
     * 作业ID
     */
    private Long homeworkId;
    
    /**
     * 学生ID
     */
    private Long studentId;
    
    /**
     * 学生姓名（关联查询）
     */
    private String studentName;
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 班级名称（关联查询）
     */
    private String className;
    
    /**
     * 提交内容
     */
    private String submitContent;
    
    /**
     * 提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime submitTime;
    
    /**
     * 是否迟交（0-否，1-是）
     */
    private Integer isLate;
    
    /**
     * 批改状态（0-未批改，1-已批改）
     */
    private Integer gradeStatus;
    
    /**
     * 成绩
     */
    private BigDecimal score;
    
    /**
     * 评语
     */
    private String comment;
    
    /**
     * 批改人ID
     */
    private Long graderId;
    
    /**
     * 批改人姓名（关联查询）
     */
    private String graderName;
    
    /**
     * 批改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime gradeTime;
}
