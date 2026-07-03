package com.edum.service;

import com.edum.entity.Notice;

import java.util.Map;

/**
 * 公告通知服务接口
 */
public interface NoticeService {

    /**
     * 分页查询公告列表
     */
    Map<String, Object> getPage(int pageNum, int pageSize, String title, Integer type, Integer status);

    /**
     * 根据ID查询公告详情
     */
    Notice getById(Long id);

    /**
     * 新增公告
     */
    void add(Notice notice);

    /**
     * 更新公告
     */
    void update(Notice notice);

    /**
     * 删除公告
     */
    void delete(Long id);
}
