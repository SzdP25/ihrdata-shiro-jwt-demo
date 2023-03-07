package com.ihrdata.demo.web.controller;

import javax.servlet.http.HttpServletRequest;

import com.github.pagehelper.PageInfo;
import com.ihrdata.demo.service.RbacRoleService;
import com.ihrdata.demo.web.param.RbacRoleAddParam;
import com.ihrdata.demo.web.param.RbacRolePagingParam;
import com.ihrdata.demo.web.vo.RbacRoleResourceVo;
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
@Api(value = "权限模块-角色管理", tags = {"权限模块角色管理"})
@RequestMapping(path = "/rbac/role")
public class RbacRoleController {
    @Autowired
    private RbacRoleService rbacRoleService;

    @ApiOperation(value = "查询角色接口(encode = true，decode = true)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/queryRole")
    @EncryptAnnotation(encode = true)
    @DecryptAnnotation(decode = true)
    public JsonResponse<PageInfo<RbacRoleResourceVo>> queryRole(@RequestBody RbacRolePagingParam param) {
        ParamValidatorUtil.validate(param);

        return JsonResponse.success("查询成功", rbacRoleService.queryAll(param));
    }

    @ApiOperation(value = "删除角色接口(不操作)", httpMethod = "GET", notes = "")
    @GetMapping(path = "/delete")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType = "query", name = "id", value = "角色ID", required = true, dataType = "Long")})
    public JsonResponse<?> delete(@RequestParam Long id, HttpServletRequest request) throws BusinessException {
        rbacRoleService.delete(id, request.getHeader("token"));

        return JsonResponse.success();
    }

    @ApiOperation(value = "保存角色接口(encode = false，decode = true)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/save")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = true)
    public JsonResponse<?> save(@RequestBody RbacRoleAddParam param, HttpServletRequest request)
        throws BusinessException {
        ParamValidatorUtil.validate(param);
        param.setToken(request.getHeader("token"));
        ParamValidatorUtil.validate(param);
        if (ValidationUtil.isEmpty(param.getId())) {
            // id为空进行添加操作
            rbacRoleService.add(param);
        } else {
            // id不为空进行修改操作
            rbacRoleService.update(param);
        }

        return JsonResponse.success();
    }
}
