package com.monitor.module.llm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 字段名称建议请求DTO
 * 后端会自动解析SQL获取字段列表
 *
 * @author monitor
 */
@Data
public class FieldNameSuggestRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 督办类型名称
     */
    @NotBlank(message = "督办类型不能为空")
    private String typeName;

    /**
     * 督办描述（可选）
     */
    private String description;

    /**
     * SQL语句
     */
    @NotBlank(message = "SQL语句不能为空")
    private String sql;
}

