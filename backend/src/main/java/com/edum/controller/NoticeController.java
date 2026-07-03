package com.edum.controller;

import com.edum.common.Result;
import com.edum.entity.Notice;
import com.edum.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 公告通知控制器
 */
@RestController
@RequestMapping("/api/notices")
@Validated
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 分页查询公告列表
     */
    @GetMapping
    public Result<Map<String, Object>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        try {
            Map<String, Object> data = noticeService.getPage(pageNum, pageSize, title, type, status);
            return Result.success(data);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据ID查询公告详情
     */
    @GetMapping("/{id}")
    public Result<Notice> getById(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getById(id);
            return Result.success(notice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 新增公告
     */
    @PostMapping
    public Result<Void> add(@RequestBody @Validated Notice notice, HttpServletRequest request) {
        try {
            // 从Token中获取发布者ID
            Long publisherId = (Long) request.getAttribute("userId");
            if (publisherId == null) {
                return Result.error("未登录");
            }

            notice.setPublisherId(publisherId);
            noticeService.add(notice);
            return Result.success("新增成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody @Validated Notice notice) {
        try {
            notice.setId(id);
            noticeService.update(notice);
            return Result.success("更新成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            noticeService.delete(id);
            return Result.success("删除成功", null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
