package com.monitor.common.enums;

/**
 * Task status enum
 * 状态：0-待分配，1-方案待提交，2-方案待评审，3-优化中，4-待验收，5-已完成
 */
public enum TaskStatusEnum {

    PENDING_ALLOCATION(0, "待分配"),
    PLAN_PENDING_SUBMIT(1, "方案待提交"),
    PLAN_PENDING_REVIEW(2, "方案待评审"),
    OPTIMIZING(3, "优化中"),
    PENDING_ACCEPTANCE(4, "待验收"),
    COMPLETED(5, "已完成");

    private final int code;
    private final String description;

    TaskStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TaskStatusEnum fromCode(int code) {
        for (TaskStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
