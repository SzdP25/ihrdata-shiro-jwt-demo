package com.ihrdata.demo.web.param;

import javax.validation.constraints.NotBlank;

import com.ihrdata.wtool.common.annotation.PhoneAnnotation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ApiModel(value = "Rbac用户添加参数对象")
public class RbacUserAddParam {
    @ApiModelProperty(value = "userID")
    private Long userId;

    @NotBlank(message = "昵称不能为空")
    @Length(min = 1, max = 20, message = "昵称长度为1~20位")
    @ApiModelProperty(value = "昵称")
    private String nickname;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 1, max = 20, message = "用户名长度为1~20位")
    @ApiModelProperty(value = "用户名")
    private String username;

    @Length(min = 1, max = 32, message = "密码长度为1~32位")
    @ApiModelProperty(value = "密码")
    private String userPassword;

    @PhoneAnnotation
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "角色Id")
    private Long roleId;

    @ApiModelProperty(value = "部门Id")
    private Long organizationId;

    @ApiModelProperty(value = "token")
    private String token;
}
