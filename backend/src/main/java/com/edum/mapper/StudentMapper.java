package com.edum.mapper;

import com.edum.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生Mapper接口
 */
@Mapper
public interface StudentMapper {
    
    /**
     * 分页查询学生列表
     */
    List<Student> selectPage(@Param("name") String name,
                              @Param("classId") Long classId,
                              @Param("offset") int offset,
                              @Param("pageSize") int pageSize);
    
    /**
     * 统计总数
     */
    long countTotal(@Param("name") String name,
                    @Param("classId") Long classId);
    
    /**
     * 根据ID查询学生详情
     */
    Student selectById(@Param("id") Long id);
    
    /**
     * 插入学生
     */
    int insert(Student student);
    
    /**
     * 更新学生
     */
    int update(Student student);
    
    /**
     * 逻辑删除学生
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 批量逻辑删除学生
     */
    int deleteBatch(@Param("ids") List<Long> ids);
    
    /**
     * 检查学号是否存在
     */
    Student checkStudentNoExists(@Param("studentNo") String studentNo, @Param("excludeId") Long excludeId);
    
    /**
     * 统计班级学生人数
     */
    int countByClassId(@Param("classId") Long classId);
    
    /**
     * 按姓名模糊搜索
     */
    List<Student> searchByName(@Param("name") String name);
    
    /**
     * 按班级筛选
     */
    List<Student> selectByClassId(@Param("classId") Long classId);
}
