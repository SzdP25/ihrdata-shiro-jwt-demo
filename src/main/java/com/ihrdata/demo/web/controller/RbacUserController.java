package com.ihrdata.demo.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.github.pagehelper.PageInfo;
import com.ihrdata.demo.service.RbacUserService;
import com.ihrdata.demo.web.param.RbacUserAddParam;
import com.ihrdata.demo.web.param.RbacUserPagingParam;
import com.ihrdata.demo.web.vo.RbacUserQueryVo;
import com.ihrdata.demo.web.vo.RbacUserVo;
import com.ihrdata.wtool.common.annotation.DecryptAnnotation;
import com.ihrdata.wtool.common.annotation.EncryptAnnotation;
import com.ihrdata.wtool.common.exception.BusinessException;
import com.ihrdata.wtool.common.jsonrespone.JsonResponse;
import com.ihrdata.wtool.common.utils.ParamValidatorUtil;
import com.ihrdata.wtool.common.utils.ValidationUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(value = "权限模块-用户管理", tags = {"权限模块用户管理"})
@RequestMapping(path = "/rbac/user")
public class RbacUserController {
    @Autowired
    private RbacUserService rbacUserService;

    @ApiOperation(value = "查询用户接口(encode = true，decode = true)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/query")
    @EncryptAnnotation(encode = true)
    @DecryptAnnotation(decode = true)
    public JsonResponse<PageInfo<RbacUserVo>> queryUser(@RequestBody RbacUserPagingParam param) {
        ParamValidatorUtil.validate(param);

        return JsonResponse.success("查询成功", rbacUserService.queryAll(param));
    }

    @ApiOperation(value = "查询全部用户接口(encode = false，decode = false)", httpMethod = "GET")
    @GetMapping(path = "/queryAll")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = false)
    public JsonResponse<List<RbacUserQueryVo>> queryAllRbacUser() {

        return JsonResponse.success("查询成功", rbacUserService.queryAllRbacUser());
    }

    @ApiOperation(value = "删除用户接口(不操作)", httpMethod = "GET", notes = "")
    @GetMapping(path = "/delete")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", name = "userId", value = "用户ID", required = true, dataType = "Long")})
    public JsonResponse<?> delete(@RequestParam Long loginUserId, @RequestParam Long userId) {
        rbacUserService.delete(loginUserId, userId);

        return JsonResponse.success();
    }

    @ApiOperation(value = "获得全部角色接口(encode = true，decode = false)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/get")
    @EncryptAnnotation(encode = true)
    @DecryptAnnotation(decode = false)
    // @RequiresPermissions("power/power-role")
    public JsonResponse<?> get() {

        return JsonResponse.success("查询成功", rbacUserService.getAll());
    }

    @ApiOperation(value = "保存用户接口(encode = false，decode = true)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/save")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = true)
    public JsonResponse<?> save(@RequestBody RbacUserAddParam param, HttpServletRequest request)
        throws BusinessException {
        ParamValidatorUtil.validate(param);
        param.setToken(request.getHeader("token"));
        if (ValidationUtil.isEmpty(param.getUserId())) {
            // id为空进行添加操作
            rbacUserService.add(param);
        } else {
            // id不为空进行修改操作
            rbacUserService.update(param);
        }

        return JsonResponse.success();
    }

    @ApiOperation(value = "修改用户状态接口(不操作)", httpMethod = "GET", notes = "")
    @GetMapping(path = "/updateStatus")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = false)
    public JsonResponse<?> updateStatus(@RequestParam Long userId, @RequestParam Byte userStatus)
        throws BusinessException {
        rbacUserService.updateStatus(userId, userStatus);

        return JsonResponse.success();
    }

    @ApiOperation(value = "重置密码接口(不操作)", httpMethod = "GET", notes = "")
    @GetMapping(path = "/resetPassword")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", name = "userId", value = "用户ID", required = true, dataType = "Long")})
    // @RequiresPermissions("power/power-role")
    public JsonResponse<?> resetPassword(@RequestParam Long userId, HttpServletRequest request) {
        ParamValidatorUtil.validate(userId);
        String token = request.getHeader("token");
        rbacUserService.resetPassword(userId,token);

        return JsonResponse.success();
    }
}
