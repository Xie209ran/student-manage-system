package com.edum.controller;

import com.edum.common.Result;
import com.edum.mapper.ClassMapper;
import com.edum.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用控制器 - 提供下拉列表等通用数据
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {
    
    @Autowired
    private ClassMapper classMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取班级下拉列表
     */
    @GetMapping("/classes")
    public Result<List<Map<String, Object>>> getClassList() {
        try {
            List<Map<String, Object>> classList = classMapper.selectDropDownList();
            return Result.success(classList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取教师下拉列表
     */
    @GetMapping("/teachers")
    public Result<List<Map<String, Object>>> getTeacherList() {
        try {
            List<Map<String, Object>> teacherList = userMapper.selectTeacherDropDownList();
            return Result.success(teacherList);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取科目列表（返回固定数组）
     */
    @GetMapping("/subjects")
    public Result<List<String>> getSubjectList() {
        try {
            List<String> subjects = new ArrayList<>();
            subjects.add("语文");
            subjects.add("数学");
            subjects.add("英语");
            subjects.add("物理");
            subjects.add("化学");
            subjects.add("生物");
            subjects.add("政治");
            subjects.add("历史");
            subjects.add("地理");
            subjects.add("音乐");
            subjects.add("美术");
            subjects.add("体育");
            subjects.add("信息技术");
            subjects.add("通用技术");
            
            return Result.success(subjects);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
