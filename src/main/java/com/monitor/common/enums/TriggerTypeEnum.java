package com.monitor.common.enums;

/**
 * Trigger type enum
 */
public enum TriggerTypeEnum {

    SCHEDULED(1, "定时"),
    MANUAL(2, "手动"),
    API(3, "API调用");

    private final int code;
    private final String description;

    TriggerTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TriggerTypeEnum fromCode(int code) {
        for (TriggerTypeEnum type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
