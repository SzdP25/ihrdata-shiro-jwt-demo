package com.ihrdata.demo.web.param;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@ApiModel(value = "账号密码参数对象（账号登录）")
public class AccountParamByAccount {
    @NotBlank(message = "用户名不能为空")
    @Length(min = 1, max = 100, message = "用户名长度为1~100位")
    @ApiModelProperty(value = "用户名")
    private String username;

    @Length(min = 1, max = 100, message = "密码长度为1~100位")
    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "验证码", required = true)
    private String verify;

    @ApiModelProperty(value = "验证码id", required = true)
    private String uuid;
}
