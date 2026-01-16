package com.monitor.module.task.controller;

import com.monitor.common.result.Result;
import com.monitor.module.task.dto.*;
import com.monitor.module.task.entity.BizTask;
import com.monitor.module.task.scheduler.TaskScheduler;
import com.monitor.module.task.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 任务控制器
 *
 * @author monitor
 */
@Slf4j
@RestController
@RequestMapping("/api/task")
@Api(tags = "任务管理")
@Validated
public class TaskController {

    private final TaskService taskService;
    private final TaskScheduler taskScheduler;

    public TaskController(TaskService taskService, TaskScheduler taskScheduler) {
        this.taskService = taskService;
        this.taskScheduler = taskScheduler;
    }

    /**
     * 创建任务
     *
     * @param dto 创建DTO
     * @return 任务ID
     */
    @PostMapping
    @ApiOperation("创建任务")
    public Result<Long> create(@RequestBody @ApiParam("任务创建信息") TaskCreateDTO dto) {
        Long id = taskService.create(dto);
        return Result.success(id);
    }

    /**
     * 获取任务详情
     *
     * @param id 任务ID
     * @return 任务详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取任务详情")
    public Result<TaskVO> getById(@PathVariable @ApiParam("任务ID") Long id) {
        TaskVO vo = taskService.getById(id);
        return Result.success(vo);
    }

    /**
     * 更新任务
     *
     * @param id  任务ID
     * @param dto 更新DTO
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @ApiOperation("更新任务")
    public Result<Void> update(
            @PathVariable @ApiParam("任务ID") Long id,
            @RequestBody @ApiParam("任务更新信息") TaskUpdateDTO dto) {
        dto.setId(id);
        taskService.update(dto);
        return Result.success();
    }

    /**
     * 删除任务
     *
     * @param id 任务ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除任务")
    public Result<Void> delete(@PathVariable @ApiParam("任务ID") Long id) {
        // 删除调度任务
        taskScheduler.deleteTask(id);
        // 删除数据库记录
        taskService.delete(id);
        return Result.success();
    }

    /**
     * 分页查询任务列表
     *
     * @param dto 列表查询DTO
     * @return 分页结果
     */
    @GetMapping("/list")
    @ApiOperation("分页查询任务列表")
    public Result<Map<String, Object>> list(@ModelAttribute @ApiParam("查询条件") TaskListDTO dto) {
        Map<String, Object> result = taskService.list(dto);
        return Result.success(result);
    }

    /**
     * 查询全部任务列表（支持分页）
     *
     * @param dto 列表查询DTO（包含filter过滤条件和分页参数）
     * @return 分页结果
     */
    @PostMapping("/all")
    @ApiOperation("查询全部任务列表（支持分页）")
    public Result<Map<String, Object>> getAll(@RequestBody @ApiParam("查询条件") TaskListDTO dto) {
        Map<String, Object> result = taskService.list(dto);
        return Result.success(result);
    }

    /**
     * 更新任务状态
     *
     * @param id     任务ID
     * @param status 状态
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @ApiOperation("更新任务状态")
    public Result<Void> updateStatus(
            @PathVariable @ApiParam("任务ID") Long id,
            @RequestParam @ApiParam("状态：0-暂停，1-运行中") Integer status) {
        taskService.updateStatus(id, status);

        // 更新调度器中的任务状态
        if (status == 1) {
            taskScheduler.resumeTask(id);
        } else {
            taskScheduler.pauseTask(id);
        }

        return Result.success();
    }

    /**
     * 手动执行任务
     *
     * @param id  任务ID
     * @param dto 执行DTO
     * @return 执行结果
     */
    @PostMapping("/{id}/execute")
    @ApiOperation("手动执行任务")
    public Result<Map<String, Object>> execute(
            @PathVariable @ApiParam("任务ID") Long id,
            @RequestBody(required = false) @ApiParam("执行参数") TaskExecuteDTO dto) {
        if (dto == null) {
            dto = new TaskExecuteDTO();
        }
        dto.setTaskId(id);
        Map<String, Object> result = taskService.execute(dto);
        return Result.success(result);
    }

    /**
     * 根据类型ID获取任务列表
     *
     * @param typeId 类型ID
     * @return 任务列表
     */
    @GetMapping("/type/{typeId}")
    @ApiOperation("根据类型ID获取任务列表")
    public Result<List<BizTask>> getByTypeId(@PathVariable @ApiParam("类型ID") Long typeId) {
        List<BizTask> tasks = taskService.getByTypeId(typeId);
        return Result.success(tasks);
    }

    /**
     * 暂停任务
     *
     * @param id 任务ID
     * @return 操作结果
     */
    @PostMapping("/{id}/pause")
    @ApiOperation("暂停任务")
    public Result<Void> pause(@PathVariable @ApiParam("任务ID") Long id) {
        taskService.updateStatus(id, 0);
        taskScheduler.pauseTask(id);
        return Result.success();
    }

    /**
     * 恢复任务
     *
     * @param id 任务ID
     * @return 操作结果
     */
    @PostMapping("/{id}/resume")
    @ApiOperation("恢复任务")
    public Result<Void> resume(@PathVariable @ApiParam("任务ID") Long id) {
        taskService.updateStatus(id, 1);
        taskScheduler.resumeTask(id);
        return Result.success();
    }
}
