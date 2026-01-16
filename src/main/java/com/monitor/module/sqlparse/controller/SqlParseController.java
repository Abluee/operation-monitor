package com.monitor.module.sqlparse.controller;

import com.monitor.common.result.Result;
import com.monitor.module.sqlparse.dto.SqlParseDTO;
import com.monitor.module.sqlparse.dto.SqlParseResult;
import com.monitor.module.sqlparse.dto.SqlPreviewDTO;
import com.monitor.module.sqlparse.dto.SqlPreviewResult;
import com.monitor.module.sqlparse.dto.SqlFieldDTO;
import com.monitor.module.sqlparse.service.SqlParseService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL解析控制器
 */
@RestController
@RequestMapping("/api/sql/parse")
public class SqlParseController {

    private final SqlParseService sqlParseService;

    public SqlParseController(SqlParseService sqlParseService) {
        this.sqlParseService = sqlParseService;
    }

    /**
     * 解析SQL字段
     *
     * @param dto SQL解析请求
     * @return 解析结果
     */
    @PostMapping("/fields")
    public Result<List<SqlFieldDTO>> parseFields(@Valid @RequestBody SqlParseDTO dto) {
        List<SqlFieldDTO> fields = sqlParseService.queryFieldsFromDb(dto.getSql());
        return Result.success(fields);
    }

    /**
     * 验证SQL语法
     *
     * @param dto SQL解析请求
     * @return 验证结果
     */
    @PostMapping("/validate")
    public Result<Map<String, Object>> validateSql(@Valid @RequestBody SqlParseDTO dto) {
        boolean isValid = sqlParseService.validateSql(dto.getSql());
        Map<String, Object> result = new HashMap<>();
        result.put("valid", isValid);
        result.put("sql", dto.getSql());
        return Result.success(result);
    }

    /**
     * 预览SQL执行结果
     *
     * @param dto SQL预览请求
     * @return 预览结果
     */
    @PostMapping("/preview")
    public Result<SqlPreviewResult> preview(@Valid @RequestBody SqlPreviewDTO dto) {
        SqlPreviewResult result = sqlParseService.preview(dto.getSql(), dto.getTimeRange());
        return Result.success(result);
    }
}
