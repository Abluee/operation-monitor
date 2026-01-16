package com.monitor.module.execute.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 执行结果实体
 * 对应 biz_exec_result 表
 *
 * @author monitor
 */
@Data
@TableName("biz_exec_result")
public class BizExecResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 类型ID
     */
    private Long typeId;

    /**
     * 执行时间
     */
    private Date execTime;

    /**
     * 数据条数
     */
    private Integer dataCount;

    /**
     * 指标数据（JSON格式）
     */
    private String metricData;

    /**
     * 阈值检查结果（JSON格式）
     */
    private String thresholdResult;

    /**
     * 是否完成：0-未完成，1-已完成
     */
    private Integer isCompleted;

    /**
     * 完成原因
     */
    private String completeReason;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
