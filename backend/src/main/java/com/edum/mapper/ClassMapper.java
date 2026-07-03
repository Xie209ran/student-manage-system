package com.edum.mapper;

import com.edum.entity.ClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级Mapper接口
 */
@Mapper
public interface ClassMapper {
    
    /**
     * 分页查询班级列表
     */
    List<ClassEntity> selectPage(@Param("className") String className,
                                  @Param("grade") Integer grade,
                                  @Param("teacherId") Long teacherId,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);
    
    /**
     * 统计总数
     */
    long countTotal(@Param("className") String className,
                    @Param("grade") Integer grade,
                    @Param("teacherId") Long teacherId);
    
    /**
     * 根据ID查询班级详情
     */
    ClassEntity selectById(@Param("id") Long id);
    
    /**
     * 插入班级
     */
    int insert(ClassEntity classEntity);
    
    /**
     * 更新班级
     */
    int update(ClassEntity classEntity);
    
    /**
     * 逻辑删除班级
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查班级名称是否存在
     */
    ClassEntity checkClassNameExists(@Param("className") String className, @Param("excludeId") Long excludeId);
    
    /**
     * 统计班级学生人数
     */
    int countStudentsByClassId(@Param("classId") Long classId);
    
    /**
     * 按年级查询班级列表
     */
    List<ClassEntity> selectByGrade(@Param("grade") Integer grade);
    
    /**
     * 按教师查询班级列表
     */
    List<ClassEntity> selectByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 获取班级下拉列表（只返回id和className）
     */
    java.util.List<java.util.Map<String, Object>> selectDropDownList();
    
    /**
     * 查询所有班级及其学生人数
     */
    java.util.List<java.util.Map<String, Object>> selectClassWithStudentCount();
}
