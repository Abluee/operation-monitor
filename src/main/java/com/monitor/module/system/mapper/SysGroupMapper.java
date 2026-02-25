package com.monitor.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.module.system.entity.SysGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 小组Mapper接口
 *
 * @author monitor
 */
@Mapper
public interface SysGroupMapper extends BaseMapper<SysGroup> {

    /**
     * 根据状态查询小组列表
     *
     * @param status 状态
     * @return 小组列表
     */
    @Select("SELECT * FROM sys_group WHERE status = #{status} ORDER BY create_time DESC")
    List<SysGroup> selectByStatus(@Param("status") Integer status);

    /**
     * 根据编码查询小组
     *
     * @param groupCode 小组编码
     * @return 小组
     */
    @Select("SELECT * FROM sys_group WHERE group_code = #{groupCode}")
    SysGroup selectByCode(@Param("groupCode") String groupCode);

    /**
     * 查询所有启用的小组
     *
     * @return 小组列表
     */
    @Select("SELECT * FROM sys_group WHERE status = 1 ORDER BY create_time DESC")
    List<SysGroup> selectAllActive();
}
