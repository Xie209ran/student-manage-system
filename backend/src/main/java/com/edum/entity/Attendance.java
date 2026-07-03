package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Attendance extends BaseEntity {
    
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
     * 考勤日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate attendanceDate;
    
    /**
     * 考勤状态（0-缺勤，1-出勤，2-迟到，3-早退，4-请假）
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 打卡人ID
     */
    private Long checkInUserId;
    
    /**
     * 打卡人姓名（关联查询）
     */
    private String checkInUserName;
    
    /**
     * 打卡时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime checkInTime;
}
