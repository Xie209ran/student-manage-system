package com.edum.service.impl;

import com.edum.entity.Notice;
import com.edum.mapper.NoticeMapper;
import com.edum.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公告通知服务实现类
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 分页查询公告列表
     */
    @Override
    public Map<String, Object> getPage(int pageNum, int pageSize, String title, Integer type, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<Notice> list = noticeMapper.selectPage(title, type, status, offset, pageSize);
        long total = noticeMapper.countTotal(title, type, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);

        return result;
    }

    /**
     * 根据ID查询公告详情
     */
    @Override
    public Notice getById(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new RuntimeException("公告不存在");
        }
        return notice;
    }

    /**
     * 新增公告
     */
    @Override
    public void add(Notice notice) {
        // 参数校验
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new RuntimeException("公告标题不能为空");
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            throw new RuntimeException("公告内容不能为空");
        }
        if (notice.getPublisherId() == null) {
            throw new RuntimeException("发布者ID不能为空");
        }

        // 设置默认值
        if (notice.getType() == null) {
            notice.setType(1);  // 默认为通知
        }
        if (notice.getStatus() == null) {
            notice.setStatus(0);  // 默认为草稿
        }
        // 如果状态为已发布，设置发布时间
        if (notice.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now());
        }

        noticeMapper.insert(notice);
    }

    /**
     * 更新公告
     */
    @Override
    public void update(Notice notice) {
        // 检查公告是否存在
        Notice existing = noticeMapper.selectById(notice.getId());
        if (existing == null) {
            throw new RuntimeException("公告不存在");
        }

        // 如果从草稿改为发布，设置发布时间
        if (existing.getStatus() == 0 && notice.getStatus() != null && notice.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now());
        }

        noticeMapper.update(notice);
    }

    /**
     * 删除公告
     */
    @Override
    public void delete(Long id) {
        // 检查公告是否存在
        Notice existing = noticeMapper.selectById(id);
        if (existing == null) {
            throw new RuntimeException("公告不存在");
        }

        noticeMapper.deleteById(id);
    }
}
