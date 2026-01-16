package com.monitor.module.sqlparse.service;

import com.monitor.module.sqlparse.dto.SqlFieldDTO;
import com.monitor.module.sqlparse.dto.SqlParseResult;
import com.monitor.module.sqlparse.dto.SqlPreviewResult;

import java.util.List;

/**
 * SQL解析服务接口
 */
public interface SqlParseService {

    /**
     * 从SQL中提取字段列表
     *
     * @param sql SQL语句
     * @return 字段列表解析结果
     */
    SqlParseResult parseFields(String sql);

    /**
     * 从数据库查询字段信息
     *
     * @param sql SQL语句
     * @return 字段列表
     */
    List<SqlFieldDTO> queryFieldsFromDb(String sql);

    /**
     * 验证SQL语法
     *
     * @param sql SQL语句
     * @return 是否有效
     */
    boolean validateSql(String sql);

    /**
     * 预览SQL执行结果
     *
     * @param sql       SQL语句
     * @param timeRange 时间范围
     * @return 预览结果
     */
    SqlPreviewResult preview(String sql, String timeRange);
}
