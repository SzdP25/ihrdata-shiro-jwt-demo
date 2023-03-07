package com.ihrdata.demo.web.vo;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(value = "Rbac用户对象", description = "Rbac用户对象")
public class RbacUserVo {
    @ApiModelProperty(value = "机构名称")
    private String organizationName;

    @ApiModelProperty(value = "机构ID")
    private Long organizationId;

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "员工姓名")
    private String nickname;

    @ApiModelProperty(value = "角色姓名")
    private String roleName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "角色Id")
    private Long roleId;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "账号名")
    private String username;

    @ApiModelProperty(value = "用户状态：1-正常，2-注销，3-停用")
    private Byte userStatus;

    @ApiModelProperty(value = "权限集合")
    private List<RbacResourcesVo> rbacResourceList;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date gmtCreate;

    @ApiModelProperty(value = "token")
    private String token;
}
