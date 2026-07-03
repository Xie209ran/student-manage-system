package com.edum.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 公告通知实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Notice extends BaseEntity {

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 发布者ID
     */
    private Long publisherId;

    /**
     * 发布者姓名（关联查询）
     */
    private String publisherName;

    /**
     * 类型（1-通知，2-公告）
     */
    private Integer type;

    /**
     * 状态（0-草稿，1-已发布）
     */
    private Integer status;

    /**
     * 发布时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime publishTime;
}
