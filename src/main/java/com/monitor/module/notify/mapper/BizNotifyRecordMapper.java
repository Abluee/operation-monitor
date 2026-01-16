package com.monitor.module.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monitor.module.notify.entity.BizNotifyRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Notification record mapper interface
 *
 * @author monitor
 */
@Mapper
public interface BizNotifyRecordMapper extends BaseMapper<BizNotifyRecord> {

    /**
     * Select notification records by task ID
     *
     * @param taskId Task ID
     * @return List of notification records
     */
    @Select("SELECT * FROM biz_notify_record WHERE task_id = #{taskId} AND deleted = 0 ORDER BY create_time DESC")
    List<BizNotifyRecord> selectByTaskId(@Param("taskId") Long taskId);

    /**
     * Select notification records by status with pagination
     *
     * @param page  Page object
     * @param status Status
     * @return Page of notification records
     */
    @Select("SELECT * FROM biz_notify_record WHERE status = #{status} AND deleted = 0 ORDER BY create_time ASC")
    IPage<BizNotifyRecord> selectByStatus(Page<BizNotifyRecord> page, @Param("status") Integer status);

    /**
     * Select failed notification records for retry
     *
     * @param taskId Task ID
     * @param maxRetryCount Maximum retry count
     * @return List of failed notification records
     */
    @Select("SELECT * FROM biz_notify_record WHERE task_id = #{taskId} AND status = #{status} AND retry_count < #{maxRetryCount} AND deleted = 0")
    List<BizNotifyRecord> selectFailedRecordsForRetry(
            @Param("taskId") Long taskId,
            @Param("status") Integer status,
            @Param("maxRetryCount") Integer maxRetryCount);
}
