package com.monitor.module.type.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务类型实体
 * 对应 biz_type 表
 *
 * @author monitor
 */
@Data
@TableName("biz_type")
public class BizType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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
     * 字段配置（JSON格式）
     */
    private String fieldConfig;

    /**
     * 验证配置（JSON格式）
     */
    private String verifyConfig;

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
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新用户ID
     */
    private Long updateUser;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识：0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
