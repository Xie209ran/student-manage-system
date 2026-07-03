package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 作业实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Homework extends BaseEntity {
    
    /**
     * 作业标题
     */
    private String title;
    
    /**
     * 作业内容
     */
    private String content;
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 班级名称（关联查询）
     */
    private String className;
    
    /**
     * 科目
     */
    private String subject;
    
    /**
     * 发布教师ID
     */
    private Long teacherId;
    
    /**
     * 发布教师姓名（关联查询）
     */
    private String teacherName;
    
    /**
     * 截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime deadline;
    
    /**
     * 作业状态（0-未开始，1-进行中，2-已结束）
     */
    private Integer status;
}
