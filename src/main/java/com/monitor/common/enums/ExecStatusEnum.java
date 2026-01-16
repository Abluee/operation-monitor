package com.monitor.common.enums;

/**
 * Execution status enum
 */
public enum ExecStatusEnum {

    FAIL(0, "失败"),
    SUCCESS(1, "成功");

    private final int code;
    private final String description;

    ExecStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ExecStatusEnum fromCode(int code) {
        for (ExecStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
