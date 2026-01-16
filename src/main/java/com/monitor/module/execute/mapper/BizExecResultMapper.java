package com.monitor.module.execute.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.module.execute.entity.BizExecResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 执行结果Mapper接口
 *
 * @author monitor
 */
@Mapper
public interface BizExecResultMapper extends BaseMapper<BizExecResult> {

    /**
     * 根据任务ID查询执行结果
     *
     * @param taskId 任务ID
     * @return 执行结果列表
     */
    @Select("SELECT * FROM biz_exec_result WHERE task_id = #{taskId} AND deleted = 0 ORDER BY exec_time DESC")
    List<BizExecResult> selectByTaskId(@Param("taskId") Long taskId);

    /**
     * 查询任务最近一次执行结果
     *
     * @param taskId 任务ID
     * @return 最近一次执行结果
     */
    @Select("SELECT * FROM biz_exec_result WHERE task_id = #{taskId} AND deleted = 0 ORDER BY exec_time DESC LIMIT 1")
    BizExecResult selectLatestByTaskId(@Param("taskId") Long taskId);
}
