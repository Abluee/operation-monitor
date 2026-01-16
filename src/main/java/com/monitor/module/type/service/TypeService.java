package com.monitor.module.type.service;

import com.monitor.module.type.dto.*;
import com.monitor.module.type.entity.BizType;

import java.util.List;
import java.util.Map;

/**
 * 业务类型服务接口
 *
 * @author monitor
 */
public interface TypeService {

    /**
     * 创建类型
     *
     * @param dto 类型创建DTO
     * @return 创建的类型ID
     */
    Long create(TypeCreateDTO dto);

    /**
     * 更新类型
     *
     * @param dto 类型更新DTO
     */
    void update(TypeUpdateDTO dto);

    /**
     * 删除类型
     *
     * @param id 类型ID
     */
    void delete(Long id);

    /**
     * 根据ID查询类型
     *
     * @param id 类型ID
     * @return 类型详情
     */
    TypeVO getById(Long id);

    /**
     * 分页查询类型列表
     *
     * @param dto 列表查询DTO
     * @return 分页结果
     */
    Map<String, Object> list(TypeListDTO dto);

    /**
     * 获取类型选项列表（下拉框用）
     *
     * @return 类型选项列表
     */
    List<Map<String, Object>> getOptions();

    /**
     * 根据类型ID导入数据（执行SQL查询并分页返回）
     *
     * @param typeId  类型ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页数据
     */
    Map<String, Object> importDataByTypeId(Long typeId, Integer pageNum, Integer pageSize);

    /**
     * 根据类型ID导入数据并批量创建任务
     *
     * @param typeId 类型ID
     * @param dto    批量导入配置
     * @return 包含typeId, typeName和查询数据列表的结果
     */
    Map<String, Object> importDataAndSaveTasks(Long typeId, TypeImportDTO dto);
}
