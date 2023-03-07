package com.ihrdata.demo.web.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.model.RbacRole;
import com.ihrdata.demo.model.RbacUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class RbacRoleVo extends RbacRole {
    private List<RbacUser> rbacUserList;
    private List<RbacResource> rbacResourceList;

    public static RbacRoleVo adapt(RbacRole rbacRole) {
        RbacRoleVo rbacRoleVo = new RbacRoleVo();

        BeanUtils.copyProperties(rbacRole, rbacRoleVo);

        return rbacRoleVo;
    }
}
