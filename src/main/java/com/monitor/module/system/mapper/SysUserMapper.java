package com.monitor.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.monitor.module.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper接口
 *
 * @author monitor
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据状态查询用户列表
     *
     * @param status 状态
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE status = #{status} ORDER BY create_time DESC")
    List<SysUser> selectByStatus(@Param("status") Integer status);

    /**
     * 根据小组ID查询用户列表
     *
     * @param groupId 小组ID
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE group_id = #{groupId} ORDER BY create_time DESC")
    List<SysUser> selectByGroupId(@Param("groupId") Long groupId);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户
     */
    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUser selectByEmail(@Param("email") String email);

    /**
     * 查询所有启用的用户
     *
     * @return 用户列表
     */
    @Select("SELECT * FROM sys_user WHERE status = 1 ORDER BY create_time DESC")
    List<SysUser> selectAllActive();

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户
     */
    @Select("SELECT * FROM sys_user WHERE phone = #{phone}")
    SysUser selectByPhone(@Param("phone") String phone);
}
