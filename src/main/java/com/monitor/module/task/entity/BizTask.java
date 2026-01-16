package com.monitor.module.task.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务实体
 * 对应 biz_task 表
 *
 * @author monitor
 */
@Data
@TableName("biz_task")
public class BizTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 类型ID
     */
    private Long typeId;

    /**
     * 类型名称（非数据库字段，关联查询）
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
     * 阈值规则配置（JSON格式）
     */
    private String thresholdRules;

    /**
     * 完成规则配置（JSON格式）
     */
    private String completeRules;

    /**
     * 查询条件配置（JSON格式）
     */
    private String queryCondition;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 状态：0-待分配，1-方案待提交，2-方案待评审，3-优化中，4-待验收，5-已完成
     */
    private Integer status;

    /**
     * 最后执行时间
     */
    private Date lastExecTime;

    /**
     * 下次执行时间
     */
    private Date nextExecTime;

    /**
     * 任务负责人ID
     */
    private Long assigneeId;

    /**
     * 任务负责组ID
     */
    private Long groupId;

    /**
     * 监控源数据（如api_slow_stat、biz_metrics_stat等表的数据，JSON格式）
     */
    private String sourceData;

    /**
     * 创建用户ID
     */
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新用户ID
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
