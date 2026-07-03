package com.edum.service.impl;

import com.edum.entity.ClassEntity;
import com.edum.entity.CourseSchedule;
import com.edum.mapper.ClassMapper;
import com.edum.mapper.CourseMapper;
import com.edum.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级服务实现类
 */
@Service
public class ClassServiceImpl implements ClassService {
    
    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    /**
     * 分页查询班级列表
     */
    @Override
    public Map<String, Object> getPage(int pageNum, int pageSize, String className, Integer grade, Long teacherId) {
        int offset = (pageNum - 1) * pageSize;
        List<ClassEntity> list = classMapper.selectPage(className, grade, teacherId, offset, pageSize);
        long total = classMapper.countTotal(className, grade, teacherId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 根据ID查询班级详情
     */
    @Override
    public ClassEntity getById(Long id) {
        ClassEntity classEntity = classMapper.selectById(id);
        if (classEntity == null) {
            throw new RuntimeException("班级不存在");
        }
        return classEntity;
    }
    
    /**
     * 新增班级
     */
    @Override
    public void add(ClassEntity classEntity) {
        // 检查班级名称是否已存在
        ClassEntity existing = classMapper.checkClassNameExists(classEntity.getClassName(), null);
        if (existing != null) {
            throw new RuntimeException("班级名称已存在");
        }
        
        // 校验年级范围
        if (classEntity.getGrade() < 1 || classEntity.getGrade() > 12) {
            throw new RuntimeException("年级必须在1-12之间");
        }
        
        // 校验最大人数
        if (classEntity.getMaxCapacity() < 10 || classEntity.getMaxCapacity() > 100) {
            throw new RuntimeException("最大人数必须在10-100之间");
        }
        
        classMapper.insert(classEntity);
    }
    
    /**
     * 更新班级
     */
    @Override
    public void update(ClassEntity classEntity) {
        // 检查班级是否存在
        ClassEntity existing = classMapper.selectById(classEntity.getId());
        if (existing == null) {
            throw new RuntimeException("班级不存在");
        }
        
        // 检查班级名称是否与其他班级重复
        ClassEntity nameExists = classMapper.checkClassNameExists(classEntity.getClassName(), classEntity.getId());
        if (nameExists != null) {
            throw new RuntimeException("班级名称已存在");
        }
        
        // 校验年级范围
        if (classEntity.getGrade() < 1 || classEntity.getGrade() > 12) {
            throw new RuntimeException("年级必须在1-12之间");
        }
        
        // 校验最大人数
        if (classEntity.getMaxCapacity() < 10 || classEntity.getMaxCapacity() > 100) {
            throw new RuntimeException("最大人数必须在10-100之间");
        }
        
        classMapper.update(classEntity);
    }
    
    /**
     * 删除班级
     */
    @Override
    public void delete(Long id) {
        // 检查班级是否存在
        ClassEntity classEntity = classMapper.selectById(id);
        if (classEntity == null) {
            throw new RuntimeException("班级不存在");
        }
        
        // 检查是否有学生
        int studentCount = classMapper.countStudentsByClassId(id);
        if (studentCount > 0) {
            throw new RuntimeException("该班级下有" + studentCount + "名学生，请先转移或删除学生");
        }
        
        // 检查是否有课程安排
        List<CourseSchedule> courses = courseMapper.selectByClassId(id);
        if (courses != null && !courses.isEmpty()) {
            throw new RuntimeException("该班级有" + courses.size() + "个课程安排，请先取消课程");
        }
        
        // 逻辑删除
        classMapper.deleteById(id);
    }
}
