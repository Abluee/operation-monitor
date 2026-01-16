package com.monitor.module.type.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.module.type.entity.BizType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 业务类型Mapper接口
 *
 * @author monitor
 */
@Mapper
public interface BizTypeMapper extends BaseMapper<BizType> {

    /**
     * 查询启用的类型列表
     *
     * @return 启用的业务类型列表
     */
    @Select("SELECT id, type_name FROM biz_type WHERE status = 1 AND deleted = 0 ORDER BY create_time DESC")
    List<Map<String, Object>> selectEnabledTypes();
}
