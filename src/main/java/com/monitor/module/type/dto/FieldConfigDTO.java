package com.monitor.module.type.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 字段配置DTO
 *
 * @author monitor
 */
@Data
public class FieldConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 查询字段列表
     */
    private List<QueryField> queryFields;

    /**
     * 表列配置列表
     */
    private List<TableColumn> tableColumns;

    /**
     * 时间字段
     */
    private String timeField;

    /**
     * 默认时间范围（分钟）
     */
    private Integer defaultTimeRange;

    /**
     * 查询字段
     */
    @Data
    public static class QueryField implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 字段名称
         */
        private String fieldName;

        /**
         * 字段标签
         */
        private String fieldLabel;

        /**
         * 字段类型
         */
        private String fieldType;

        /**
         * 是否必填
         */
        private Boolean required;

        /**
         * 默认值
         */
        private Object defaultValue;
    }

    /**
     * 表列配置
     */
    @Data
    public static class TableColumn implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 列键
         */
        private String columnKey;

        /**
         * 列标题
         */
        private String columnTitle;

        /**
         * 列宽度
         */
        private Integer columnWidth;

        /**
         * 是否可排序
         */
        private Boolean sortable;

        /**
         * 对齐方式：left, center, right
         */
        private String align;
    }
}
