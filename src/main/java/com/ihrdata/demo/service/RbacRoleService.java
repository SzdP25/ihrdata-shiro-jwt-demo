package com.ihrdata.demo.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.ihrdata.demo.model.RbacRole;
import com.ihrdata.demo.web.param.RbacRoleAddParam;
import com.ihrdata.demo.web.param.RbacRolePagingParam;
import com.ihrdata.demo.web.vo.RbacRoleResourceVo;
import com.ihrdata.demo.web.vo.RbacRoleVo;

/**
 * 角色service
 *
 * @Author wangwz
 * @Date 2020/11/24
 */
public interface RbacRoleService {
    /**
     * 获取全部角色权限(分页)
     */
    PageInfo<RbacRoleResourceVo> queryAll(RbacRolePagingParam param);

    /**
     * 根据角色名查询角色(分页)
     */
    PageInfo<RbacRole> query(RbacRolePagingParam param);

    /**
     * 添加角色
     */
    void add(RbacRoleAddParam param);

    /**
     * 根据ID获取角色信息
     */
    RbacRoleVo get(Long id);

    /**
     * 修改角色信息
     */
    void update(RbacRoleAddParam param);

    /**
     * 删除角色
     */
    void delete(Long id, String token);

    /**
     * 获取用户下全部角色
     */
    List<RbacRole> getByUserId(Long userId);

    /**
     * 根据角色ID获取角色
     */
    RbacRole getByRoleId(Long roleId);

}