package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成绩实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Score extends BaseEntity {
    
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
     * 科目
     */
    private String subject;
    
    /**
     * 考试名称
     */
    private String examName;
    
    /**
     * 考试成绩
     */
    private BigDecimal score;
    
    /**
     * 满分
     */
    private BigDecimal fullScore;
    
    /**
     * 及格分数
     */
    private BigDecimal passScore;
    
    /**
     * 排名
     */
    private Integer rank;
    
    /**
     * 录入人ID
     */
    private Long inputUserId;
    
    /**
     * 录入人姓名（关联查询）
     */
    private String inputUserName;
    
    /**
     * 修改人ID
     */
    private Long updateUserId;
    
    /**
     * 修改人姓名（关联查询）
     */
    private String updateUserName;
    
    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime modifyTime;
}
