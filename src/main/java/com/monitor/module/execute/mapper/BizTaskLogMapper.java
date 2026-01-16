package com.monitor.module.execute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.module.execute.entity.BizTaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 任务日志Mapper接口
 *
 * @author monitor
 */
@Mapper
public interface BizTaskLogMapper extends BaseMapper<BizTaskLog> {

    /**
     * 根据任务ID查询执行日志
     *
     * @param taskId 任务ID
     * @return 执行日志列表
     */
    @Select("SELECT * FROM biz_task_log WHERE task_id = #{taskId} AND deleted = 0 ORDER BY exec_time DESC")
    List<BizTaskLog> selectByTaskId(@Param("taskId") Long taskId);

    /**
     * 根据时间范围查询执行日志
     *
     * @param taskId    任务ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 执行日志列表
     */
    @Select("SELECT * FROM biz_task_log WHERE task_id = #{taskId} AND exec_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY exec_time DESC")
    List<BizTaskLog> selectByTimeRange(@Param("taskId") Long taskId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
