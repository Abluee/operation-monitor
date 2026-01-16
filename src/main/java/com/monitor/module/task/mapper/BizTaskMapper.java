package com.monitor.module.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.module.task.dto.TaskFilterDTO;
import com.monitor.module.task.entity.BizTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 任务Mapper接口
 *
 * @author monitor
 */
@Mapper
public interface BizTaskMapper extends BaseMapper<BizTask> {

    /**
     * 根据状态查询任务列表
     *
     * @param status 状态
     * @return 任务列表
     */
    @Select("SELECT * FROM biz_task WHERE status = #{status} ORDER BY next_exec_time ASC")
    List<BizTask> selectByStatus(@Param("status") Integer status);

    /**
     * 根据负责人ID查询任务列表
     *
     * @param assigneeId 负责人ID
     * @return 任务列表
     */
    @Select("SELECT * FROM biz_task WHERE assignee_id = #{assigneeId} ORDER BY create_time DESC")
    List<BizTask> selectByAssignee(@Param("assigneeId") Long assigneeId);

    /**
     * 根据类型ID查询任务列表
     *
     * @param typeId 类型ID
     * @return 任务列表
     */
    @Select("SELECT * FROM biz_task WHERE type_id = #{typeId} ORDER BY create_time DESC")
    List<BizTask> selectByTypeId(@Param("typeId") Long typeId);

    /**
     * 查询需要执行的任务列表
     *
     * @return 任务列表
     */
    @Select("SELECT * FROM biz_task WHERE status = 1 AND next_exec_time <= NOW() ORDER BY next_exec_time ASC")
    List<BizTask> selectTasksToExecute();

    /**
     * 根据ID查询任务（带类型信息）
     *
     * @param id 任务ID
     * @return 任务
     */
    BizTask selectByIdWithType(@Param("id") Long id);

    /**
     * 查询所有任务（带类型信息）
     *
     * @return 任务列表
     */
    List<BizTask> selectAllWithType();

    /**
     * 条件查询任务列表（带类型信息）
     *
     * @param typeId     类型ID
     * @param status     状态
     * @param taskName   任务名称
     * @param assigneeId 负责人ID
     * @param groupId    组ID
     * @param filter     过滤条件列表
     * @return 任务列表
     */
    List<BizTask> selectListWithType(@Param("typeId") Long typeId,
                                      @Param("status") Integer status,
                                      @Param("taskName") String taskName,
                                      @Param("assigneeId") Long assigneeId,
                                      @Param("groupId") Long groupId,
                                      @Param("filter") List<TaskFilterDTO> filter);
}
