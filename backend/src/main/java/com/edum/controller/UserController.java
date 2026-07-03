package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.SysUser;
import com.edum.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器 - 教师管理
 */
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询教师列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> data = userService.getPage(page, pageSize, name, status);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询教师详情
     */
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        try {
            SysUser user = userService.getById(id);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 新增教师
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Validated SysUser user) {
        try {
            // 参数校验
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
                return Result.error("教师姓名不能为空");
            }
            
            userService.add(user);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新教师信息
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Validated SysUser user) {
        try {
            if (user.getId() == null) {
                return Result.error("用户ID不能为空");
            }
            
            userService.update(user);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
