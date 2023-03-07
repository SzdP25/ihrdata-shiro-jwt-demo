package com.ihrdata.demo.web.vo;

import java.util.List;

import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.model.RbacRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "角色权限视图对象")
public class RbacRoleResourceVo extends RbacRole {
    @ApiModelProperty(value = "权限集合")
    private List<RbacResource> resources;
}
