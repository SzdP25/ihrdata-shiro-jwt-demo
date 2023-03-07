package com.ihrdata.demo.dao;

import java.util.List;

import com.ihrdata.demo.model.RbacRole;
import com.ihrdata.demo.web.param.RbacRolePagingParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacRoleCustomMapper {
    List<RbacRole> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询全部角色
     *
     * @Author wangwz
     * @Date 2020/12/23
     * @Params @param rbacRolePagingParam:
     * @return java.util.List<com.ihrdata.demo.model.RbacRole>
     */
    List<RbacRole> selectAllRole(@Param("rbacRolePagingParam") RbacRolePagingParam rbacRolePagingParam);
}
