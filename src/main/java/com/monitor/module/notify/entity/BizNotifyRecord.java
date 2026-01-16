package com.monitor.module.notify.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Notification record entity
 * Corresponding to biz_notify_record table
 *
 * @author monitor
 */
@Data
@TableName("biz_notify_record")
public class BizNotifyRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * Task ID
     */
    private Long taskId;

    /**
     * Task name
     */
    private String taskName;

    /**
     * Notification channel (dingtalk, email, webhook)
     */
    private String channel;

    /**
     * Notification type: 1-Threshold Alert, 2-Task Complete, 3-Custom
     */
    private Integer notifyType;

    /**
     * Notification content (JSON format)
     */
    private String content;

    /**
     * Status: 0-Pending, 1-Sent, 2-Failed, 3-Retrying
     */
    private Integer status;

    /**
     * Error message (if failed)
     */
    private String errorMsg;

    /**
     * Retry count
     */
    private Integer retryCount;

    /**
     * Send time
     */
    private Date sendTime;

    /**
     * Create time
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * Update time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * Notification status constants
     */
    public interface Status {
        int PENDING = 0;
        int SENT = 1;
        int FAILED = 2;
        int RETRYING = 3;
    }

    /**
     * Notification type constants
     */
    public interface NotifyType {
        int THRESHOLD_ALERT = 1;
        int TASK_COMPLETE = 2;
        int CUSTOM = 3;
    }
}
