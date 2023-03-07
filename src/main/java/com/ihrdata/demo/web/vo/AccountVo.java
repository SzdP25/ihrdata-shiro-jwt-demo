package com.ihrdata.demo.web.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.model.RbacRole;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
@ApiModel(value = "账户首页视图对象")
public class AccountVo {
    @ApiModelProperty(value = "账户信息")
    private RbacUserVo rbacUserVo;

    @ApiModelProperty(value = "账户所拥角色")
    private List<RbacRole> rbacRoleList;

    @ApiModelProperty(value = "账户所拥资源")
    private List<RbacResource> rbacResourceList;
}
