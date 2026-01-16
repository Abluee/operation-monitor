package com.monitor.module.execute.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 任务执行日志实体
 * 对应 biz_task_log 表
 *
 * @author monitor
 */
@Data
@TableName("biz_task_log")
public class BizTaskLog implements Serializable {

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
     * 执行状态：0-失败，1-成功
     */
    private Integer execStatus;

    /**
     * 触发类型：1-定时，2-手动，3-API调用
     */
    private Integer triggerType;

    /**
     * 执行的SQL语句
     */
    private String execSql;

    /**
     * 执行结果（JSON格式）
     */
    private String execResult;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 执行耗时（毫秒）
     */
    private Integer durationMs;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
