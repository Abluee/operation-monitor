package com.monitor.module.task.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 任务响应VO
 *
 * @author monitor
 */
@Data
public class TaskVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 类型ID
     */
    private Long typeId;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 阈值规则配置
     */
    private Map<String, Object> thresholdRules;

    /**
     * 完成规则配置
     */
    private Map<String, Object> completeRules;

    /**
     * 查询条件配置
     */
    private Map<String, Object> queryCondition;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 状态：0-待分配，1-方案待提交，2-方案待评审，3-优化中，4-待验收，5-已完成
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 最后执行时间
     */
    private Date lastExecTime;

    /**
     * 格式化最后执行时间
     */
    private String lastExecTimeStr;

    /**
     * 下次执行时间
     */
    private Date nextExecTime;

    /**
     * 格式化下次执行时间
     */
    private String nextExecTimeStr;

    /**
     * 任务负责人ID
     */
    private Long assigneeId;

    /**
     * 任务负责人名称
     */
    private String assigneeName;

    /**
     * 任务负责组ID
     */
    private Long groupId;

    /**
     * 任务负责组名称
     */
    private String groupName;

    /**
     * 创建用户ID
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 格式化创建时间
     */
    private String createTimeStr;

    /**
     * 更新用户ID
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 格式化更新时间
     */
    private String updateTimeStr;

    /**
     * 监控源数据（JSON格式）
     */
    private String sourceData;
}
