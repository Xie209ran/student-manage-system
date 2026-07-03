package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学生实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends BaseEntity {
    
    /**
     * 学号
     */
    private String studentNo;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 性别（0-女，1-男）
     */
    private Integer gender;
    
    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate birthDate;
    
    /**
     * 班级ID
     */
    private Long classId;
    
    /**
     * 班级名称（关联查询）
     */
    private String className;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 家庭住址
     */
    private String address;
    
    /**
     * 家长姓名
     */
    private String parentName;
    
    /**
     * 家长联系电话
     */
    private String parentPhone;
}
