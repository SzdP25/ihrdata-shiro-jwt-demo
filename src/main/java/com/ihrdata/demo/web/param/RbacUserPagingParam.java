package com.ihrdata.demo.web.param;

import com.ihrdata.wtool.web.param.PagingParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "权限用户分页对象", description = "权限用户分页对象")
public class RbacUserPagingParam extends PagingParam {
    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称", required = false)
    private String nickname;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = false)
    private String username;

    /**
     * 角色Id
     */
    @ApiModelProperty(value = "角色Id", required = false)
    private Long roleId;

    /**
     * 角色Id
     */
    @ApiModelProperty(value = "部门Id", required = false)
    private Long organizationId;

    /**
     * 用户状态
     */
    @ApiModelProperty(value = "用户状态", required = false)
    private Byte userStatus;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", required = false)
    private String gmtCreate;
}
