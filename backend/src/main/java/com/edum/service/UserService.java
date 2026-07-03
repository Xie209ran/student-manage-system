package com.edum.service;

import com.edum.entity.SysUser;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    Map<String, Object> login(String username, String password);
    
    /**
     * 退出登录
     */
    void logout(Long userId);
    
    /**
     * 获取当前用户信息
     */
    SysUser getCurrentUser(Long userId);
    
    /**
     * 分页查询教师列表
     */
    java.util.Map<String, Object> getPage(int pageNum, int pageSize, String name, Integer status);
    
    /**
     * 根据ID查询教师详情
     */
    SysUser getById(Long id);
    
    /**
     * 新增教师
     */
    void add(SysUser user);
    
    /**
     * 更新教师信息
     */
    void update(SysUser user);
}
