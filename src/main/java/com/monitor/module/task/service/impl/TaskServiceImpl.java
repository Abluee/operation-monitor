package com.monitor.module.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.monitor.common.enums.TaskStatusEnum;
import com.monitor.common.exception.BizException;
import com.monitor.common.utils.JsonUtils;
import com.monitor.module.task.dto.*;
import com.monitor.module.task.entity.BizTask;
import com.monitor.module.task.mapper.BizTaskMapper;
import com.monitor.module.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 任务服务实现
 *
 * @author monitor
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<BizTaskMapper, BizTask> implements TaskService {

    private final BizTaskMapper bizTaskMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TaskServiceImpl(BizTaskMapper bizTaskMapper) {
        this.bizTaskMapper = bizTaskMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TaskCreateDTO dto) {
        BizTask bizTask = new BizTask();
        BeanUtils.copyProperties(dto, bizTask);
        bizTask.setStatus(TaskStatusEnum.PENDING_ALLOCATION.getCode()); // 默认待分配
        bizTask.setCreateTime(new Date());
        bizTask.setCreateUser(getCurrentUserId());

        // 设置JSON字段
        bizTask.setThresholdRules(JsonUtils.toJSONString(dto.getThresholdRules()));
        bizTask.setCompleteRules(JsonUtils.toJSONString(dto.getCompleteRules()));
        bizTask.setQueryCondition(JsonUtils.toJSONString(dto.getQueryCondition()));

        bizTaskMapper.insert(bizTask);
        log.info("创建任务成功，id: {}, taskName: {}", bizTask.getId(), bizTask.getTaskName());
        return bizTask.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TaskUpdateDTO dto) {
        // 验证任务是否存在
        BizTask existing = bizTaskMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BizException("任务不存在");
        }

        BizTask bizTask = new BizTask();
        BeanUtils.copyProperties(dto, bizTask);
        bizTask.setUpdateTime(new Date());
        bizTask.setUpdateUser(getCurrentUserId());

        // 设置JSON字段
        bizTask.setThresholdRules(JsonUtils.toJSONString(dto.getThresholdRules()));
        bizTask.setCompleteRules(JsonUtils.toJSONString(dto.getCompleteRules()));
        bizTask.setQueryCondition(JsonUtils.toJSONString(dto.getQueryCondition()));

        bizTaskMapper.updateById(bizTask);
        log.info("更新任务成功，id: {}, taskName: {}", dto.getId(), dto.getTaskName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        BizTask existing = bizTaskMapper.selectById(id);
        if (existing == null) {
            throw new BizException("任务不存在");
        }

        // 物理删除
        bizTaskMapper.deleteById(id);
        log.info("删除任务成功，id: {}", id);
    }

    @Override
    public TaskVO getById(Long id) {
        BizTask bizTask = bizTaskMapper.selectByIdWithType(id);
        if (bizTask == null) {
            throw new BizException("任务不存在");
        }
        return convertToVO(bizTask);
    }

    @Override
    public Map<String, Object> list(TaskListDTO dto) {
        // 确保分页参数有默认值
        int pageNum = dto.getPageNum() != null ? dto.getPageNum() : 1;
        int pageSize = dto.getPageSize() != null ? dto.getPageSize() : 10;

        // 查询所有带类型信息的任务
        List<BizTask> allTasks = bizTaskMapper.selectListWithType(
                dto.getTypeId(),
                dto.getStatus(),
                dto.getTaskName(),
                dto.getAssigneeId(),
                dto.getGroupId(),
                dto.getFilter()
        );

        // Java端分页
        int total = allTasks.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<BizTask> pageRecords = fromIndex < total
                ? allTasks.subList(fromIndex, toIndex)
                : Collections.emptyList();

        // 转换为VO列表
        List<TaskVO> voList = pageRecords.stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("list", voList);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        BizTask existing = bizTaskMapper.selectById(id);
        if (existing == null) {
            throw new BizException("任务不存在");
        }

        BizTask bizTask = new BizTask();
        bizTask.setId(id);
        bizTask.setStatus(status);
        bizTask.setUpdateTime(new Date());
        bizTask.setUpdateUser(getCurrentUserId());

        bizTaskMapper.updateById(bizTask);
        log.info("更新任务状态成功，id: {}, status: {}", id, status);
    }

    @Override
    public List<BizTask> getByTypeId(Long typeId) {
        return bizTaskMapper.selectByTypeId(typeId);
    }

    @Override
    public Map<String, Object> execute(TaskExecuteDTO dto) {
        BizTask task = bizTaskMapper.selectById(dto.getTaskId());
        if (task == null) {
            throw new BizException("任务不存在");
        }

        // TODO: 实现任务执行逻辑
        // 1. 执行SQL查询
        // 2. 应用阈值规则
        // 3. 应用完成规则
        // 4. 更新执行时间

        Map<String, Object> result = new HashMap<>();
        result.put("taskId", dto.getTaskId());
        result.put("execTime", new Date());
        result.put("status", "success");

        // 更新最后执行时间
        BizTask updateTask = new BizTask();
        updateTask.setId(dto.getTaskId());
        updateTask.setLastExecTime(new Date());
        updateTask.setUpdateTime(new Date());
        updateTask.setUpdateUser(getCurrentUserId());
        bizTaskMapper.updateById(updateTask);

        log.info("手动执行任务成功，taskId: {}", dto.getTaskId());
        return result;
    }

    /**
     * 转换为VO
     *
     * @param bizTask 实体
     * @return VO
     */
    private TaskVO convertToVO(BizTask bizTask) {
        TaskVO vo = new TaskVO();
        BeanUtils.copyProperties(bizTask, vo);

        // 设置状态描述
        TaskStatusEnum statusEnum = TaskStatusEnum.fromCode(bizTask.getStatus());
        vo.setStatusDesc(statusEnum != null ? statusEnum.getDescription() : "未知");

        // 格式化时间
        if (bizTask.getCreateTime() != null) {
            vo.setCreateTimeStr(bizTask.getCreateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DATE_TIME_FORMATTER));
        }
        if (bizTask.getUpdateTime() != null) {
            vo.setUpdateTimeStr(bizTask.getUpdateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DATE_TIME_FORMATTER));
        }
        if (bizTask.getLastExecTime() != null) {
            vo.setLastExecTimeStr(bizTask.getLastExecTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DATE_TIME_FORMATTER));
        }
        if (bizTask.getNextExecTime() != null) {
            vo.setNextExecTimeStr(bizTask.getNextExecTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DATE_TIME_FORMATTER));
        }

        // TODO: 设置assigneeName, groupName（需要关联查询）

        return vo;
    }

    /**
     * 获取当前用户ID（临时实现，需要根据实际认证机制调整）
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        // TODO: 从安全上下文获取当前用户ID
        return 1L;
    }
}
