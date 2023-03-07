package com.ihrdata.demo.dao;

import java.util.List;

import com.ihrdata.demo.model.RbacUser;
import com.ihrdata.demo.web.param.RbacUserPagingParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RbacUserCustomMapper {

    List<RbacUser> selectByRoleId(@Param("roleId") Long roleId);

    List<RbacUser> selectAllRole(@Param("rbacUserPagingParam") RbacUserPagingParam param);
}
