package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.ClassEntity;
import com.edum.service.ClassService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 班级控制器
 */
@RestController
@RequestMapping("/api/classes")
@Validated
public class ClassController {
    
    @Autowired
    private ClassService classService;
    
    /**
     * 分页查询班级列表
     */
    @GetMapping
    public Result<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String className,
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) Long teacherId) {
        try {
            Map<String, Object> data = classService.getPage(pageNum, pageSize, className, grade, teacherId);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询班级详情
     */
    @GetMapping("/{id}")
    public Result<ClassEntity> getById(@PathVariable Long id) {
        try {
            ClassEntity classEntity = classService.getById(id);
            return Result.success(classEntity);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 新增班级
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Validated ClassEntity classEntity) {
        try {
            // 参数校验
            if (classEntity.getClassName() == null || classEntity.getClassName().trim().isEmpty()) {
                return Result.error("班级名称不能为空");
            }
            if (classEntity.getGrade() == null) {
                return Result.error("年级不能为空");
            }
            if (classEntity.getMaxCapacity() == null) {
                return Result.error("最大人数不能为空");
            }
            
            classService.add(classEntity);
            return Result.success("新增成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新班级
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated ClassEntity classEntity) {
        try {
            classEntity.setId(id);
            
            // 参数校验
            if (classEntity.getClassName() == null || classEntity.getClassName().trim().isEmpty()) {
                return Result.error("班级名称不能为空");
            }
            if (classEntity.getGrade() == null) {
                return Result.error("年级不能为空");
            }
            if (classEntity.getMaxCapacity() == null) {
                return Result.error("最大人数不能为空");
            }
            
            classService.update(classEntity);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除班级
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            classService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
