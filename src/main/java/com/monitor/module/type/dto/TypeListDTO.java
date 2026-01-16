package com.monitor.module.type.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 类型列表查询DTO
 *
 * @author monitor
 */
@Data
public class TypeListDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类型名称（模糊查询）
     */
    @Size(max = 50, message = "类型名称长度不能超过50个字符")
    private String typeName;

    /**
     * 状态：0-禁用，1-启用，null-全部
     */
    private Integer status;

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 10;
}
