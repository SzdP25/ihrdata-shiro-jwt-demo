package com.ihrdata.demo.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ihrdata.demo.service.AccountService;
import com.ihrdata.demo.web.param.AccountParamByAccount;
import com.ihrdata.demo.web.param.AccountParamByPhone;
import com.ihrdata.demo.web.pojo.VerifyCodePojo;
import com.ihrdata.demo.web.vo.RbacUserVo;
import com.ihrdata.demo.web.vo.VerifyCodeVo;
import com.ihrdata.wtool.common.annotation.DecryptAnnotation;
import com.ihrdata.wtool.common.annotation.EncryptAnnotation;
import com.ihrdata.wtool.common.jsonrespone.JsonResponse;
import com.ihrdata.wtool.common.utils.ParamValidatorUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(value = "用户登录相关Controller", tags = {"用户登录相关操作接口"})
public class AccountController {
    @Autowired
    private AccountService accountService;

    /**
     * 生成验证码
     */
    @ApiOperation(value = "生成验证码(encode = false，decode = false)", httpMethod = "GET")
    @GetMapping(value = "/getVerify")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = false)
    public JsonResponse<?> getVerify() {
        VerifyCodePojo captcha = accountService.cacheRedis();
        //输出验证码图片方法
        return JsonResponse.success("SUCCESS", new VerifyCodeVo(captcha.getUuid(), captcha.getBaseStr()));

    }

    @ApiOperation(value = "用户账号登录接口(encode = true，decode = true)", httpMethod = "POST")
    @PostMapping(path = "/loginByAccount")
    @EncryptAnnotation(encode = true)
    @DecryptAnnotation(decode = true)
    public JsonResponse<?> loginByAccount(@RequestBody AccountParamByAccount accountParamByAccount,
        HttpServletResponse response) {
        ParamValidatorUtil.validate(accountParamByAccount);
        RbacUserVo login = accountService.loginByAccount(accountParamByAccount);
        response.setHeader("Access-Control-Expose-Headers", "token");
        response.setHeader("token", login.getToken());
        login.setToken(null);
        return JsonResponse.success("登录成功", login);
    }

    @ApiOperation(value = "手机号登录接口(encode = true，decode = true)", httpMethod = "POST")
    @PostMapping(path = "/loginByPhone")
    @EncryptAnnotation(encode = true)
    @DecryptAnnotation(decode = true)
    public JsonResponse<?> loginByPhone(@RequestBody AccountParamByPhone accountParamByPhone,
        HttpServletResponse response) {
        ParamValidatorUtil.validate(accountParamByPhone);

        RbacUserVo login = accountService.loginByPhone(accountParamByPhone);
        response.setHeader("Access-Control-Expose-Headers", "token");
        response.setHeader("token", login.getToken());
        login.setToken(null);
        return JsonResponse.success("登录成功", login);
    }

    @ApiOperation(value = "用户修改密码接口(encode = false，decode = true)", httpMethod = "GET", notes = "")
    @GetMapping(path = "/changePassword")
    // @EncryptAnnotation(encode = true)
    @DecryptAnnotation(decode = true)
    public JsonResponse<?> changePassword(@RequestParam String newPassword,
        @RequestParam String oldPassword, HttpServletRequest request) {

        accountService.changePassword(oldPassword, newPassword,request.getHeader("token"));
        return JsonResponse.success();
    }

    @ApiOperation(value = "用户登出接口(不操作)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/logout")
    public JsonResponse<?> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        accountService.logout(token);
        return JsonResponse.success();
    }

}
