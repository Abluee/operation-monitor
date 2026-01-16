package com.monitor.module.task.service;

import com.monitor.module.task.dto.*;
import com.monitor.module.task.entity.BizTask;

import java.util.List;
import java.util.Map;

/**
 * 任务服务接口
 *
 * @author monitor
 */
public interface TaskService {

    /**
     * 创建任务
     *
     * @param dto 创建DTO
     * @return 任务ID
     */
    Long create(TaskCreateDTO dto);

    /**
     * 更新任务
     *
     * @param dto 更新DTO
     */
    void update(TaskUpdateDTO dto);

    /**
     * 删除任务
     *
     * @param id 任务ID
     */
    void delete(Long id);

    /**
     * 根据ID获取任务
     *
     * @param id 任务ID
     * @return 任务VO
     */
    TaskVO getById(Long id);

    /**
     * 分页查询任务列表
     *
     * @param dto 列表查询DTO
     * @return 分页结果
     */
    Map<String, Object> list(TaskListDTO dto);

    /**
     * 更新任务状态
     *
     * @param id     任务ID
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据类型ID获取任务列表
     *
     * @param typeId 类型ID
     * @return 任务列表
     */
    List<BizTask> getByTypeId(Long typeId);

    /**
     * 手动执行任务
     *
     * @param dto 执行DTO
     * @return 执行结果
     */
    Map<String, Object> execute(TaskExecuteDTO dto);
}
