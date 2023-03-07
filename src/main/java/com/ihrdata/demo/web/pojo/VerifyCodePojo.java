package com.ihrdata.demo.web.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VerifyCodePojo {
    //验证码id
    private String uuid;
    //验证码的值
    private String verifyCode;
    //验证码串
    private String baseStr;
}