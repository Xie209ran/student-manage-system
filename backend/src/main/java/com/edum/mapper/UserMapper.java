package com.edum.mapper;

import com.edum.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询用户
     */
    SysUser findByUsername(@Param("username") String username);
    
    /**
     * 根据ID查询用户
     */
    SysUser findById(@Param("id") Long id);
    
    /**
     * 插入用户
     */
    int insert(SysUser user);
    
    /**
     * 更新用户
     */
    int update(SysUser user);
    
    /**
     * 更新登录失败次数
     */
    int updateLoginFailCount(@Param("id") Long id, @Param("loginFailCount") Integer loginFailCount);
    
    /**
     * 更新锁定时间
     */
    int updateLockedUntil(@Param("id") Long id, @Param("lockedUntil") java.time.LocalDateTime lockedUntil);
    
    /**
     * 更新最后登录时间
     */
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") java.time.LocalDateTime lastLoginTime);
    
    /**
     * 重置登录失败次数
     */
    int resetLoginFailCount(@Param("id") Long id);
    
    /**
     * 获取教师下拉列表（只返回id和realName，role='teacher'）
     */
    java.util.List<java.util.Map<String, Object>> selectTeacherDropDownList();
    
    /**
     * 分页查询教师列表
     */
    java.util.List<SysUser> selectPage(@Param("name") String name,
                                       @Param("status") Integer status,
                                       @Param("offset") int offset,
                                       @Param("pageSize") int pageSize);
    
    /**
     * 统计教师总数
     */
    long countTotal(@Param("name") String name,
                    @Param("status") Integer status);
}
