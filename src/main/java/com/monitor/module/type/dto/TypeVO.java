package com.monitor.module.type.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类型响应VO
 *
 * @author monitor
 */
@Data
public class TypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 类型描述
     */
    private String description;

    /**
     * SQL内容
     */
    private String sqlContent;

    /**
     * 字段配置
     */
    private List<Map<String, Object>> fieldConfig;

    /**
     * 验证配置
     */
    private Map<String, Object> verifyConfig;

    /**
     * 计算公式
     */
    private String formula;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建用户ID
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 格式化创建时间
     */
    private String createTimeStr;

    /**
     * 更新用户ID
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 格式化更新时间
     */
    private String updateTimeStr;
}
