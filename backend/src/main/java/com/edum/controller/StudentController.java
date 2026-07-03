package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.Student;
import com.edum.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    /**
     * 分页查询学生列表
     */
    @GetMapping
    public Result<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long classId) {
        try {
            Map<String, Object> data = studentService.getPage(pageNum, pageSize, name, classId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询学生详情
     */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        try {
            Student student = studentService.getById(id);
            return Result.success(student);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 新增学生
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Validated Student student) {
        try {
            // 参数校验
            if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
                return Result.error("学号不能为空");
            }
            if (student.getName() == null || student.getName().trim().isEmpty()) {
                return Result.error("姓名不能为空");
            }
            if (student.getClassId() == null) {
                return Result.error("班级不能为空");
            }
            
            studentService.add(student);
            return Result.success("新增成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新学生
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated Student student) {
        try {
            student.setId(id);
            
            // 参数校验
            if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
                return Result.error("学号不能为空");
            }
            if (student.getName() == null || student.getName().trim().isEmpty()) {
                return Result.error("姓名不能为空");
            }
            if (student.getClassId() == null) {
                return Result.error("班级不能为空");
            }
            
            studentService.update(student);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除学生
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            studentService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除学生
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        try {
            studentService.deleteBatch(ids);
            return Result.success("批量删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
