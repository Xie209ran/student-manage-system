package com.edum.mapper;

import com.edum.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公告通知Mapper接口
 */
@Mapper
public interface NoticeMapper {

    /**
     * 分页查询公告列表
     */
    List<Notice> selectPage(@Param("title") String title,
                            @Param("type") Integer type,
                            @Param("status") Integer status,
                            @Param("offset") int offset,
                            @Param("pageSize") int pageSize);

    /**
     * 统计总数
     */
    long countTotal(@Param("title") String title,
                    @Param("type") Integer type,
                    @Param("status") Integer status);

    /**
     * 根据ID查询公告详情
     */
    Notice selectById(@Param("id") Long id);

    /**
     * 插入公告
     */
    int insert(Notice notice);

    /**
     * 更新公告
     */
    int update(Notice notice);

    /**
     * 逻辑删除公告
     */
    int deleteById(@Param("id") Long id);
}
