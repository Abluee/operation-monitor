package com.monitor.module.llm.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 字段名称建议响应DTO
 *
 * @author monitor
 */
@Data
public class FieldNameSuggestResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名称建议列表
     */
    private List<FieldSuggestion> suggestions;

    /**
     * 字段名称建议
     */
    @Data
    public static class FieldSuggestion implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 字段英文名称
         */
        private String name;

        /**
         * 建议的中文名称
         */
        private String chineseName;

        /**
         * 数据类型
         */
        private String dataType;

        /**
         * 建议说明
         */
        private String reason;
    }
}

