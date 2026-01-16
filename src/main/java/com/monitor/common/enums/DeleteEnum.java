package com.monitor.common.enums;

/**
 * Delete flag enum
 */
public enum DeleteEnum {

    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final int code;
    private final String description;

    DeleteEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static DeleteEnum fromCode(int code) {
        for (DeleteEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
