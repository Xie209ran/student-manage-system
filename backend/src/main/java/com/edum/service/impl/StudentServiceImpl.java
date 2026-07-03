package com.edum.service.impl;

import com.edum.entity.ClassEntity;
import com.edum.entity.Student;
import com.edum.mapper.ClassMapper;
import com.edum.mapper.StudentMapper;
import com.edum.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Autowired
    private ClassMapper classMapper;
    
    /**
     * 分页查询学生列表
     */
    @Override
    public Map<String, Object> getPage(int pageNum, int pageSize, String name, Long classId) {
        int offset = (pageNum - 1) * pageSize;
        List<Student> list = studentMapper.selectPage(name, classId, offset, pageSize);
        long total = studentMapper.countTotal(name, classId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        
        return result;
    }
    
    /**
     * 根据ID查询学生详情
     */
    @Override
    public Student getById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        return student;
    }
    
    /**
     * 新增学生
     */
    @Override
    public void add(Student student) {
        // 检查学号是否已存在
        Student existing = studentMapper.checkStudentNoExists(student.getStudentNo(), null);
        if (existing != null) {
            throw new RuntimeException("该学号已存在");
        }
        
        // 检查班级是否存在并验证容量
        if (student.getClassId() != null) {
            ClassEntity classEntity = classMapper.selectById(student.getClassId());
            if (classEntity == null) {
                throw new RuntimeException("班级不存在");
            }
            
            // 检查班级是否满员
            int currentCount = studentMapper.countByClassId(student.getClassId());
            if (currentCount >= classEntity.getMaxCapacity()) {
                throw new RuntimeException("该班级已满员（" + currentCount + "/" + classEntity.getMaxCapacity() + "）");
            }
        }
        
        // 校验姓名长度
        if (student.getName() == null || student.getName().trim().length() < 2) {
            throw new RuntimeException("姓名长度不能少于2个字符");
        }
        
        studentMapper.insert(student);
    }
    
    /**
     * 更新学生
     */
    @Override
    public void update(Student student) {
        // 检查学生是否存在
        Student existing = studentMapper.selectById(student.getId());
        if (existing == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // 检查学号是否与其他学生重复
        Student noExists = studentMapper.checkStudentNoExists(student.getStudentNo(), student.getId());
        if (noExists != null) {
            throw new RuntimeException("该学号已存在");
        }
        
        // 校验姓名长度
        if (student.getName() == null || student.getName().trim().length() < 2) {
            throw new RuntimeException("姓名长度不能少于2个字符");
        }
        
        studentMapper.update(student);
    }
    
    /**
     * 删除学生
     */
    @Override
    public void delete(Long id) {
        // 检查学生是否存在
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // TODO: 检查是否有未批改作业（需要HomeworkSubmissionMapper）
        // int pendingHomeworkCount = homeworkSubmissionMapper.countPendingByStudentId(id);
        // if (pendingHomeworkCount > 0) {
        //     throw new RuntimeException("该学生有" + pendingHomeworkCount + "个待批改作业，请先处理");
        // }
        
        // TODO: 检查是否有考勤记录（需要AttendanceMapper）
        // int attendanceCount = attendanceMapper.countByStudentId(id);
        // if (attendanceCount > 0) {
        //     throw new RuntimeException("该学生有考勤记录，无法删除");
        // }
        
        // 逻辑删除
        studentMapper.deleteById(id);
    }
    
    /**
     * 批量删除学生
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的学生");
        }
        
        // 检查每个学生是否存在依赖
        for (Long id : ids) {
            Student student = studentMapper.selectById(id);
            if (student == null) {
                throw new RuntimeException("学生ID=" + id + "不存在");
            }
            
            // TODO: 检查是否有未批改作业
            // int pendingHomeworkCount = homeworkSubmissionMapper.countPendingByStudentId(id);
            // if (pendingHomeworkCount > 0) {
            //     throw new RuntimeException("学生" + student.getName() + "有" + pendingHomeworkCount + "个待批改作业，请先处理");
            // }
        }
        
        // 批量逻辑删除
        studentMapper.deleteBatch(ids);
    }
}
