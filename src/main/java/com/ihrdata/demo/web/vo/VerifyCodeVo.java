package com.ihrdata.demo.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(value = "验证码对象")
public class VerifyCodeVo {
    @ApiModelProperty(value = "验证码id")
    private String uuid;
    @ApiModelProperty(value = "验证码串")
    private String baseStr;
}
