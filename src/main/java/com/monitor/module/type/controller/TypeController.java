package com.monitor.module.type.controller;

import com.monitor.common.result.Result;
import com.monitor.module.type.dto.*;
import com.monitor.module.type.entity.BizType;
import com.monitor.module.type.service.TypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 业务类型控制器
 *
 * @author monitor
 */
@Slf4j
@RestController
@RequestMapping("/api/type")
@Api(tags = "业务类型管理")
@Validated
public class TypeController {

    private final TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    /**
     * 创建类型
     *
     * @param dto 创建DTO
     * @return 类型ID
     */
    @PostMapping
    @ApiOperation("创建类型")
    public Result<Long> create(@RequestBody @ApiParam("类型创建信息") TypeCreateDTO dto) {
        Long id = typeService.create(dto);
        return Result.success(id);
    }

    /**
     * 获取类型详情
     *
     * @param id 类型ID
     * @return 类型详情
     */
    @GetMapping("/{id}")
    @ApiOperation("获取类型详情")
    public Result<TypeVO> getById(@PathVariable @ApiParam("类型ID") Long id) {
        TypeVO vo = typeService.getById(id);
        return Result.success(vo);
    }

    /**
     * 更新类型
     *
     * @param id  类型ID
     * @param dto 更新DTO
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @PostMapping("/{id}")
    @ApiOperation("更新类型")
    public Result<Void> update(
            @PathVariable @ApiParam("类型ID") Long id,
            @RequestBody @ApiParam("类型更新信息") TypeUpdateDTO dto) {
        dto.setId(id);
        typeService.update(dto);
        return Result.success();
    }

    /**
     * 删除类型
     *
     * @param id 类型ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除类型")
    public Result<Void> delete(@PathVariable @ApiParam("类型ID") Long id) {
        typeService.delete(id);
        return Result.success();
    }

    /**
     * 分页查询类型列表
     *
     * @param dto 列表查询DTO
     * @return 分页结果
     */
    @GetMapping("/list")
    @ApiOperation("分页查询类型列表")
    public Result<Map<String, Object>> list(@ModelAttribute @ApiParam("查询条件") TypeListDTO dto) {
        Map<String, Object> result = typeService.list(dto);
        return Result.success(result);
    }

    /**
     * 获取类型选项列表
     *
     * @return 类型选项列表
     */
    @GetMapping("/options")
    @ApiOperation("获取类型选项列表")
    public Result<List<Map<String, Object>>> getOptions() {
        List<Map<String, Object>> options = typeService.getOptions();
        return Result.success(options);
    }

    /**
     * 根据类型ID导入数据（执行SQL查询并分页返回）
     *
     * @param typeId   类型ID
     * @param pageNum  页码，默认1
     * @param pageSize 每页大小，默认10
     * @return 分页数据
     */
    @GetMapping("/{typeId}/import")
    @ApiOperation("根据类型ID导入数据")
    public Result<Map<String, Object>> importData(
            @PathVariable @ApiParam("类型ID") Long typeId,
            @RequestParam(defaultValue = "1") @ApiParam("页码") Integer pageNum,
            @RequestParam(defaultValue = "10") @ApiParam("每页大小") Integer pageSize) {
        Map<String, Object> result = typeService.importDataByTypeId(typeId, pageNum, pageSize);
        return Result.success(result);
    }

    /**
     * 根据类型ID导入数据并批量插入到任务表
     *
     * @param dto  批量导入DTO
     * @return 导入结果
     */
    @PostMapping("/import")
    @ApiOperation("根据类型ID导入数据并批量创建任务")
    public Result<Map<String, Object>> importAndSaveTasks(
            @RequestBody @ApiParam("批量导入配置") TypeImportDTO dto) {
        Map<String, Object> result = typeService.importDataAndSaveTasks(dto.getTypeId(), dto);
        return Result.success(result);
    }
}
