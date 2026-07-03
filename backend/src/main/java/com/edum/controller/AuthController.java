package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.SysUser;
import com.edum.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        try {
            System.out.println("收到登录请求: " + loginRequest);
            
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            
            System.out.println("用户名: " + username);
            
            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                return Result.error("密码不能为空");
            }
            
            Map<String, Object> result = userService.login(username, password);
            System.out.println("登录成功");
            return Result.success("登录成功", result);
        } catch (RuntimeException e) {
            System.err.println("登录失败: " + e.getMessage());
            e.printStackTrace();
            return Result.error(e.getMessage());
        } catch (Exception e) {
            System.err.println("登录异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId != null) {
            userService.logout(userId);
        }
        return Result.success("退出成功", null);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<SysUser> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.unauthorized("未登录");
        }
        
        SysUser user = userService.getCurrentUser(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        return Result.success(user);
    }
}
