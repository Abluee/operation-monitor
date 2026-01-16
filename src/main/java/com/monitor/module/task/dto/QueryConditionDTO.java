package com.monitor.module.task.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 查询条件配置DTO
 *
 * @author monitor
 */
@Data
public class QueryConditionDTO {

    /**
     * 条件类型：and, or
     */
    @NotBlank(message = "条件类型不能为空")
    private String conditionType;

    /**
     * 条件列表
     */
    private List<ConditionItem> conditions;

    /**
     * 条件项
     */
    @Data
    public static class ConditionItem implements java.io.Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 字段名
         */
        @NotBlank(message = "字段名不能为空")
        private String field;

        /**
         * 操作符：eq, ne, gt, lt, gte, lte, like, in, between
         */
        @NotBlank(message = "操作符不能为空")
        private String operator;

        /**
         * 值
         */
        @Size(max = 500, message = "值长度不能超过500个字符")
        private Object value;
    }
}
