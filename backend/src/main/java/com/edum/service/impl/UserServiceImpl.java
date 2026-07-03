package com.edum.service.impl;

import com.edum.entity.SysUser;
import com.edum.mapper.UserMapper;
import com.edum.service.UserService;
import com.edum.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * 用户登录
     */
    @Override
    public Map<String, Object> login(String username, String password) {
        // 1. 查询用户是否存在
        SysUser user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 2. 检查账户是否被禁用
        if (user.getStatus() == 0) {
            throw new RuntimeException("账户已被禁用，请联系管理员");
        }
        
        // 3. 检查账户是否被锁定
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            long minutes = java.time.Duration.between(LocalDateTime.now(), user.getLockedUntil()).toMinutes();
            throw new RuntimeException("账户已锁定，请" + minutes + "分钟后重试");
        }
        
        // 4. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            // 登录失败，累加失败次数
            handleLoginFail(user);
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 5. 登录成功，重置失败次数
        userMapper.resetLoginFailCount(user.getId());
        
        // 6. 更新最后登录时间
        userMapper.updateLastLoginTime(user.getId(), LocalDateTime.now());
        
        // 7. 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        
        // 8. 返回Token和用户信息（不包含密码）
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        userInfo.put("avatar", user.getAvatar());
        result.put("userInfo", userInfo);
        
        return result;
    }
    
    /**
     * 处理登录失败
     */
    private void handleLoginFail(SysUser user) {
        int failCount = user.getLoginFailCount() == null ? 1 : user.getLoginFailCount() + 1;
        userMapper.updateLoginFailCount(user.getId(), failCount);
        
        // 连续失败5次，锁定30分钟
        if (failCount >= 5) {
            LocalDateTime lockedUntil = LocalDateTime.now().plusMinutes(30);
            userMapper.updateLockedUntil(user.getId(), lockedUntil);
        }
    }
    
    /**
     * 退出登录
     */
    @Override
    public void logout(Long userId) {
        // JWT是无状态的，这里可以记录到黑名单或者什么都不做
        // 前端删除Token即可
    }
    
    /**
     * 获取当前用户信息
     */
    @Override
    public SysUser getCurrentUser(Long userId) {
        SysUser user = userMapper.findById(userId);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
        }
        return user;
    }
    
    /**
     * 分页查询教师列表
     */
    @Override
    public Map<String, Object> getPage(int pageNum, int pageSize, String name, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        java.util.List<SysUser> list = userMapper.selectPage(name, status, offset, pageSize);
        long total = userMapper.countTotal(name, status);
        
        // 清除密码字段
        list.forEach(user -> user.setPassword(null));
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }
    
    /**
     * 根据ID查询教师详情
     */
    @Override
    public SysUser getById(Long id) {
        SysUser user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    /**
     * 新增教师
     */
    @Override
    public void add(SysUser user) {
        // 检查用户名是否已存在
        SysUser existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 设置默认值
        user.setRole("teacher");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setLoginFailCount(0);
        user.setStatus(user.getStatus() == null ? 1 : user.getStatus());
        
        userMapper.insert(user);
    }
    
    /**
     * 更新教师信息
     */
    @Override
    public void update(SysUser user) {
        SysUser existUser = userMapper.findById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 不允许修改用户名和密码
        user.setUsername(null);
        user.setPassword(null);
        
        userMapper.update(user);
    }
}
