package com.monitor.module.type.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 类型批量导入DTO
 *
 * @author monitor
 */
@Data
public class TypeImportDTO {

    /**
     * 源数据类型ID
     */
    @NotNull(message = "类型ID不能为空")
    private Long typeId;

    /**
     * 源数据列表（JSON格式）
     */
    @NotBlank(message = "源数据不能为空")
    private String sourceData;

    /**
     * 阈值规则配置（JSON格式）
     */
    private String thresholdRules;

    /**
     * 完成规则配置（JSON格式）
     */
    private String completeRules;

    /**
     * 查询条件配置（JSON格式）
     */
    private String queryCondition;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 任务状态：0-待分配，1-方案待提交，2-方案待评审，3-优化中，4-待验收，5-已完成
     * 默认值：0（待分配）
     */
    private Integer status = 0;
}
