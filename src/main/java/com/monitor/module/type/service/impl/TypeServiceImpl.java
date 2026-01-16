package com.monitor.module.type.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monitor.common.exception.BizException;
import com.monitor.common.utils.JsonUtils;
import com.monitor.module.task.entity.BizTask;
import com.monitor.module.task.mapper.BizTaskMapper;
import com.monitor.module.type.dto.*;
import com.monitor.module.type.entity.BizType;
import com.monitor.module.type.mapper.BizTypeMapper;
import com.monitor.module.type.service.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;

/**
 * 业务类型服务实现
 *
 * @author monitor
 */
@Slf4j
@Service
public class TypeServiceImpl extends ServiceImpl<BizTypeMapper, BizType> implements TypeService {

    private final BizTypeMapper bizTypeMapper;
    private final BizTaskMapper bizTaskMapper;
    private final ObjectMapper objectMapper;
    private final DataSource parseDataSource;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MAX_ROWS = 10000;

    public TypeServiceImpl(BizTypeMapper bizTypeMapper, BizTaskMapper bizTaskMapper,
                          ObjectMapper objectMapper,
                          @Qualifier("parseDataSource") DataSource parseDataSource) {
        this.bizTypeMapper = bizTypeMapper;
        this.bizTaskMapper = bizTaskMapper;
        this.objectMapper = objectMapper;
        this.parseDataSource = parseDataSource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(TypeCreateDTO dto) {
        BizType bizType = new BizType();
        BeanUtils.copyProperties(dto, bizType);
        bizType.setStatus(1); // 默认启用
        bizType.setCreateTime(new Date());
        bizType.setCreateUser(getCurrentUserId());

        // 设置JSON字段
        setJsonFields(bizType, dto);

        bizTypeMapper.insert(bizType);
        log.info("创建类型成功，id: {}, typeName: {}", bizType.getId(), bizType.getTypeName());
        return bizType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TypeUpdateDTO dto) {
        // 验证类型是否存在
        BizType existing = bizTypeMapper.selectById(dto.getId());
        if (existing == null) {
            throw new BizException("类型不存在");
        }

        BizType bizType = new BizType();
        BeanUtils.copyProperties(dto, bizType);
        bizType.setUpdateTime(new Date());
        bizType.setUpdateUser(getCurrentUserId());

        // 设置JSON字段
        setJsonFields(bizType, dto);

        bizTypeMapper.updateById(bizType);
        log.info("更新类型成功，id: {}, typeName: {}", dto.getId(), dto.getTypeName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        BizType existing = bizTypeMapper.selectById(id);
        if (existing == null) {
            throw new BizException("类型不存在");
        }

        // 物理删除
        bizTypeMapper.deleteById(id);
        log.info("删除类型成功，id: {}", id);
    }

    @Override
    public TypeVO getById(Long id) {
        BizType bizType = bizTypeMapper.selectById(id);
        if (bizType == null) {
            throw new BizException("类型不存在");
        }
        return convertToVO(bizType);
    }

    @Override
    public Map<String, Object> list(TypeListDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<BizType> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getTypeName())) {
            queryWrapper.like(BizType::getTypeName, dto.getTypeName());
        }
        if (dto.getStatus() != null) {
            queryWrapper.eq(BizType::getStatus, dto.getStatus());
        }

        queryWrapper.orderByDesc(BizType::getCreateTime);

        // 分页查询
        Page<BizType> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<BizType> pageResult = bizTypeMapper.selectPage(page, queryWrapper);

        // 转换为VO列表
        List<TypeVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("pageNum", pageResult.getCurrent());
        result.put("pageSize", pageResult.getSize());
        result.put("list", voList);

        return result;
    }

    @Override
    public List<Map<String, Object>> getOptions() {
        return bizTypeMapper.selectEnabledTypes();
    }

    @Override
    public Map<String, Object> importDataByTypeId(Long typeId, Integer pageNum, Integer pageSize) {
        // 查询类型信息
        BizType bizType = bizTypeMapper.selectById(typeId);
        if (bizType == null) {
            throw new BizException("类型不存在");
        }

        // 解析 fieldConfig 获取查询字段列表
        List<Map<String, Object>> fieldConfigList = null;
        if (StringUtils.hasText(bizType.getFieldConfig())) {
            try {
                fieldConfigList = objectMapper.readValue(bizType.getFieldConfig(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            } catch (IOException e) {
                log.warn("解析fieldConfig失败: {}", e.getMessage());
            }
        }

        // 获取 formula 作为查询条件
        String formula = bizType.getFormula();

        // 执行SQL查询
        List<Map<String, Object>> dataList = new ArrayList<>();
        long total = 0;

        // 构建查询SQL：根据 fieldConfig.sqlField 动态生成字段列表
        String querySql = buildQuerySql(bizType.getSqlContent(), fieldConfigList, formula, pageNum, pageSize);
        log.info("查询SQL: {}", querySql);

        try (Connection conn = parseDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnLabel(i));
            }

            int rowCount = 0;
            while (rs.next() && rowCount < MAX_ROWS) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = columns.get(i - 1);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                dataList.add(row);
                rowCount++;
            }

            // 查询总数
            String countSql = buildCountSql(bizType.getSqlContent(), fieldConfigList, formula);
            log.info("统计SQL: {}", countSql);
            try (Statement countStmt = conn.createStatement();
                 ResultSet countRs = countStmt.executeQuery(countSql)) {
                if (countRs.next()) {
                    total = countRs.getLong(1);
                }
            }

        } catch (SQLException e) {
            throw new BizException("SQL查询失败: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("list", dataList);
        result.put("typeId", typeId);
        result.put("typeName", bizType.getTypeName());

        return result;
    }

    /**
     * 构建查询SQL：根据 fieldConfig.sqlField 生成字段列表，formula 作为查询条件
     *
     * @param sqlContent      SQL内容（表名或SELECT语句）
     * @param fieldConfigList 字段配置列表
     * @param formula         查询条件
     * @param pageNum         页码
     * @param pageSize        每页大小
     * @return 完整查询SQL
     */
    private String buildQuerySql(String sqlContent, List<Map<String, Object>> fieldConfigList,
                                  String formula, Integer pageNum, Integer pageSize) {
        // 去除末尾分号
        sqlContent = sqlContent.trim();
        if (sqlContent.endsWith(";")) {
            sqlContent = sqlContent.substring(0, sqlContent.length() - 1);
        }

        // 判断是否是完整的 SELECT 语句
        boolean isSelectSql = sqlContent.toUpperCase().startsWith("SELECT");

        String selectFields = buildSqlFields(fieldConfigList);
        String whereClause = "";
        if (StringUtils.hasText(formula)) {
            whereClause = " AND " + formula;
        }

        int offset = (pageNum - 1) * pageSize;

        if (isSelectSql) {
            // 完整 SELECT 语句，替换 SELECT * 为指定字段
            String resultSql = sqlContent.replaceFirst("(?i)SELECT\\s+\\*\\s+FROM", "SELECT " + selectFields + " FROM");
            // 如果没有 WHERE，添加 WHERE；否则在末尾添加 AND 条件
            if (!resultSql.toUpperCase().contains(" WHERE ")) {
                whereClause = " WHERE " + whereClause.substring(5); // 去掉开头的 AND
            }
            return resultSql + whereClause + " LIMIT " + offset + ", " + pageSize;
        } else {
            // 简单表名
            return "SELECT " + selectFields + " FROM " + sqlContent + whereClause + " LIMIT " + offset + ", " + pageSize;
        }
    }

    /**
     * 构建统计SQL
     */
    private String buildCountSql(String sqlContent, List<Map<String, Object>> fieldConfigList, String formula) {
        // 去除末尾分号
        sqlContent = sqlContent.trim();
        if (sqlContent.endsWith(";")) {
            sqlContent = sqlContent.substring(0, sqlContent.length() - 1);
        }

        boolean isSelectSql = sqlContent.toUpperCase().startsWith("SELECT");
        String whereClause = "";
        if (StringUtils.hasText(formula)) {
            whereClause = " AND " + formula;
        }

        if (isSelectSql) {
            // 完整 SELECT 语句
            String countSql = "SELECT COUNT(*) FROM (" + sqlContent + ") _temp";
            if (StringUtils.hasText(formula)) {
                countSql = "SELECT COUNT(*) FROM (" + sqlContent + ") _temp WHERE " + formula;
            }
            return countSql;
        } else {
            // 简单表名
            if (StringUtils.hasText(formula)) {
                whereClause = " WHERE " + whereClause.substring(5);
            }
            return "SELECT COUNT(*) FROM " + sqlContent + whereClause;
        }
    }

    /**
     * 根据 fieldConfig 构建 SQL 字段列表
     *
     * @param fieldConfigList 字段配置列表
     * @return 字段列表字符串
     */
    private String buildSqlFields(List<Map<String, Object>> fieldConfigList) {
        if (fieldConfigList == null || fieldConfigList.isEmpty()) {
            return "*";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fieldConfigList.size(); i++) {
            Map<String, Object> fieldConfig = fieldConfigList.get(i);
            String sqlField = (String) fieldConfig.get("sqlField");
            if (sqlField == null || sqlField.isEmpty()) {
                sqlField = (String) fieldConfig.get("fieldName");
            }
            if (sqlField != null) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(sqlField);
            }
        }
        return sb.length() > 0 ? sb.toString() : "*";
    }

    /**
     * 设置JSON字段
     *
     * @param bizType 实体
     * @param dto     DTO
     */
    private void setJsonFields(BizType bizType, Object dto) {
        // 字段配置
        if (dto instanceof TypeCreateDTO) {
            TypeCreateDTO createDTO = (TypeCreateDTO) dto;
            bizType.setFieldConfig(createDTO.getFieldConfig());
            bizType.setVerifyConfig(createDTO.getVerifyConfig());
        } else if (dto instanceof TypeUpdateDTO) {
            TypeUpdateDTO updateDTO = (TypeUpdateDTO) dto;
            bizType.setFieldConfig(updateDTO.getFieldConfig());
            bizType.setVerifyConfig(updateDTO.getVerifyConfig());
        }
    }

    /**
     * 转换为VO
     *
     * @param bizType 实体
     * @return VO
     */
    private TypeVO convertToVO(BizType bizType) {
        TypeVO vo = new TypeVO();
        BeanUtils.copyProperties(bizType, vo);

        // 解析 fieldConfig JSON 字符串为 List<Map>
        if (StringUtils.hasText(bizType.getFieldConfig())) {
            try {
                vo.setFieldConfig(objectMapper.readValue(bizType.getFieldConfig(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class)));
            } catch (IOException e) {
                log.warn("解析fieldConfig失败: {}", e.getMessage());
            }
        }

        // 解析 verifyConfig JSON 字符串为 Map
        if (StringUtils.hasText(bizType.getVerifyConfig())) {
            try {
                vo.setVerifyConfig(objectMapper.readValue(bizType.getVerifyConfig(), Map.class));
            } catch (IOException e) {
                log.warn("解析verifyConfig失败: {}", e.getMessage());
            }
        }

        // 格式化时间
        if (bizType.getCreateTime() != null) {
            vo.setCreateTimeStr(bizType.getCreateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DATE_TIME_FORMATTER));
        }
        if (bizType.getUpdateTime() != null) {
            vo.setUpdateTimeStr(bizType.getUpdateTime().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(DATE_TIME_FORMATTER));
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> importDataAndSaveTasks(Long typeId, TypeImportDTO dto) {
        // 查询类型信息获取typeName
        BizType bizType = bizTypeMapper.selectById(typeId);
        if (bizType == null) {
            throw new BizException("类型不存在");
        }
        String typeName = bizType.getTypeName();

        // 解析 sourceData JSON 列表
        List<Map<String, Object>> dataList = null;
        if (StringUtils.hasText(dto.getSourceData())) {
            try {
                dataList = objectMapper.readValue(dto.getSourceData(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            } catch (IOException e) {
                throw new BizException("解析sourceData失败: " + e.getMessage());
            }
        }

        if (dataList == null || dataList.isEmpty()) {
            throw new BizException("源数据为空");
        }

        Long userId = getCurrentUserId();
        Date now = new Date();
        int count = 0;

        // 状态：0-待分配，1-方案待提交，2-方案待评审，3-优化中，4-待验收，5-已完成
        int status = dto.getStatus() != null ? dto.getStatus() : 0;

        // 批量创建任务
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> row = dataList.get(i);
            BizTask task = new BizTask();

            // 设置基本信息
            task.setTypeId(typeId);
            task.setStatus(status);
            task.setCreateTime(now);
            task.setCreateUser(userId);
            task.setUpdateTime(now);
            task.setUpdateUser(userId);

            // 任务名称：类型名称 + 数据标识
            String dataIdentifier = String.valueOf(row.get("id"));
            if (!row.containsKey("id") || dataIdentifier == null || "null".equals(dataIdentifier)) {
                dataIdentifier = String.valueOf(i + 1);
            }
            task.setTaskName(typeName + "-" + dataIdentifier);

            // 设置配置字段
            task.setThresholdRules(dto.getThresholdRules());
            task.setCompleteRules(dto.getCompleteRules());
            task.setQueryCondition(dto.getQueryCondition());
            task.setFormula(dto.getFormula());

            // 存储源数据到 sourceData 字段
            try {
                task.setSourceData(objectMapper.writeValueAsString(row));
            } catch (JsonProcessingException e) {
                log.warn("序列化sourceData失败: {}", e.getMessage());
            }

            bizTaskMapper.insert(task);
            count++;
        }

        log.info("批量创建任务完成，共创建 {} 个任务，typeId: {}, status: {}", count, typeId, status);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("typeId", typeId);
        result.put("typeName", typeName);
        result.put("status", status);
        result.put("statusDesc", getStatusDesc(status));
        result.put("count", count);
        return result;
    }

    /**
     * 获取状态描述
     *
     * @param status 状态码
     * @return 状态描述
     */
    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0: return "待分配";
            case 1: return "方案待提交";
            case 2: return "方案待评审";
            case 3: return "优化中";
            case 4: return "待验收";
            case 5: return "已完成";
            default: return "未知";
        }
    }

    /**
     * 获取当前用户ID（临时实现，需要根据实际认证机制调整）
     *
     * @return 用户ID
     */
    private Long getCurrentUserId() {
        // TODO: 从安全上下文获取当前用户ID
        return 1L;
    }
}
