package com.monitor.module.sqlparse.service.impl;

import com.monitor.common.exception.BizException;
import com.monitor.module.sqlparse.dto.SqlFieldDTO;
import com.monitor.module.sqlparse.dto.SqlParseResult;
import com.monitor.module.sqlparse.dto.SqlPreviewResult;
import com.monitor.module.sqlparse.service.SqlParseService;
import com.monitor.module.sqlparse.util.SqlFieldExtractor;
import com.monitor.module.sqlparse.util.SqlPreviewExecutor;
import com.monitor.module.sqlparse.util.SqlValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL解析服务实现
 */
@Service
public class SqlParseServiceImpl implements SqlParseService {

    private final SqlFieldExtractor sqlFieldExtractor;
    private final SqlValidator sqlValidator;
    private final SqlPreviewExecutor sqlPreviewExecutor;
    private final DataSource parseDataSource;

    public SqlParseServiceImpl(SqlFieldExtractor sqlFieldExtractor,
                               SqlValidator sqlValidator,
                               SqlPreviewExecutor sqlPreviewExecutor,
                               @Qualifier("parseDataSource") DataSource parseDataSource) {
        this.sqlFieldExtractor = sqlFieldExtractor;
        this.sqlValidator = sqlValidator;
        this.sqlPreviewExecutor = sqlPreviewExecutor;
        this.parseDataSource = parseDataSource;
    }

    @Override
    public SqlParseResult parseFields(String sql) {
        List<SqlFieldDTO> fields = sqlFieldExtractor.parseSelectFields(sql);
        List<String> placeholders = sqlFieldExtractor.identifyPlaceholders(sql);
        String sqlType = sqlValidator.validateSelectStatement(sql) ? "SELECT" : "UNKNOWN";

        return new SqlParseResult(fields, placeholders, sqlType);
    }

    @Override
    public List<SqlFieldDTO> queryFieldsFromDb(String sql) {
        List<SqlFieldDTO> fields = new ArrayList<>();

        // 使用 WHERE 1=2 只获取字段元数据，不返回数据
        String querySql = buildMetaQuerySql(sql);

        try (Connection conn = parseDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String sqlField = metaData.getColumnLabel(i);
                String name = sqlField;
                String dataType = mapToDataType(metaData.getColumnType(i));
                fields.add(new SqlFieldDTO(sqlField, name, dataType));
            }

        } catch (SQLException e) {
            throw new BizException("查询字段信息失败: " + e.getMessage());
        }

        return fields;
    }

    /**
     * 构建查询字段元数据的SQL
     * 使用 WHERE 1=2 只获取字段元数据，不返回数据
     */
    private String buildMetaQuerySql(String sql) {
        String upperSql = sql.toUpperCase().trim();

        if (upperSql.contains("WHERE")) {
            // 已有 WHERE，追加 AND 1=2
            int whereIndex = upperSql.indexOf("WHERE");
            return sql.substring(0, whereIndex + 5) + " 1=2 AND " + sql.substring(whereIndex + 5);
        } else if (upperSql.contains("LIMIT")) {
            // 有 LIMIT，移除 LIMIT 并添加 WHERE 1=2
            int limitIndex = upperSql.indexOf("LIMIT");
            String beforeLimit = sql.substring(0, limitIndex).trim();
            // 找到 FROM 的位置
            int fromIndex = upperSql.indexOf("FROM");
            if (fromIndex == -1) {
                throw new BizException("无法识别的SQL语句，缺少FROM关键字");
            }
            return beforeLimit + " WHERE 1=2";
        } else {
            // 没有 WHERE 和 LIMIT，添加 WHERE 1=2
            int fromIndex = upperSql.indexOf("FROM");
            if (fromIndex == -1) {
                throw new BizException("无法识别的SQL语句，缺少FROM关键字");
            }
            String beforeFrom = sql.substring(0, fromIndex).trim();
            String afterFrom = sql.substring(fromIndex + 4).trim();
            return beforeFrom + " FROM " + afterFrom + " WHERE 1=2";
        }
    }

    /**
     * 映射JDBC类型到前端类型
     */
    private String mapToDataType(int sqlType) {
        switch (sqlType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                return "string";
            case Types.BIT:
            case Types.BOOLEAN:
                return "boolean";
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.REAL:
            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.NUMERIC:
                return "number";
            case Types.DATE:
            case Types.TIMESTAMP:
            case Types.TIME:
                return "date";
            case Types.BLOB:
            case Types.CLOB:
            case Types.NCLOB:
                return "binary";
            default:
                return "string";
        }
    }

    @Override
    public boolean validateSql(String sql) {
        return sqlValidator.validateBasicSyntax(sql) && sqlValidator.validateSelectStatement(sql);
    }

    @Override
    public SqlPreviewResult preview(String sql, String timeRange) {
        if (!validateSql(sql)) {
            throw new IllegalArgumentException("SQL语法无效");
        }

        return sqlPreviewExecutor.executePreview(sql, timeRange, 10);
    }
}
