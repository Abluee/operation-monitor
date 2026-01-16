package com.monitor.module.type.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 类型创建DTO
 *
 * @author monitor
 */
@Data
public class TypeCreateDTO {

    /**
     * 类型名称
     */
    @NotBlank(message = "类型名称不能为空")
    @Size(max = 50, message = "类型名称长度不能超过50个字符")
    private String typeName;

    /**
     * 类型描述
     */
    @Size(max = 500, message = "类型描述长度不能超过500个字符")
    private String description;

    /**
     * SQL内容
     */
    private String sqlContent;

    /**
     * 字段配置（JSON字符串）
     */
    private String fieldConfig;

    /**
     * 验证配置（JSON字符串）
     */
    private String verifyConfig;

    /**
     * 计算公式
     */
    @Size(max = 500, message = "计算公式长度不能超过500个字符")
    private String formula;
}
