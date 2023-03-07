package com.ihrdata.demo.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "账号密码参数对象（手机号登录）")
public class AccountParamByPhone {
    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "短信验证码")
    private String smsVerify;
}
