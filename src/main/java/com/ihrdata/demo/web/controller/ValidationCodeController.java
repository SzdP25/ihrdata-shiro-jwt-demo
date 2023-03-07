package com.ihrdata.demo.web.controller;

import com.ihrdata.demo.service.ValidationCodeService;
import com.ihrdata.wtool.common.jsonrespone.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/validationCode")
@Api(value = "获取验证码信息controller", tags = {"获取验证码信息相关操作接口"})
public class ValidationCodeController {

    @Autowired
    private ValidationCodeService validationCodeService;

    /**
     * 获取验证码
     *
     * @return
     */
    @ApiOperation(value = "获取验证码", httpMethod = "GET", notes = "")
    @GetMapping(value = "/getValidationCode")
    public JsonResponse<?> validationCode(@RequestParam String phone) {
        validationCodeService.validationCode(phone);
        return JsonResponse.success();
    }

}
