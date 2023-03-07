package com.ihrdata.demo.web.controller;

import com.ihrdata.demo.service.RbacOrganizationService;
import com.ihrdata.demo.web.param.RbacOrganizationParam;
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
@Api(value = "部门相关Controller", tags = {"部门相关操作接口"})
@RequestMapping(path = "/rbac/organization")
public class RbacOrganizationController {
    @Autowired
    private RbacOrganizationService rbacOrganizationService;

    @ApiOperation(value = "查看全部部门接口(encode = true，decode = true)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/queryAll")
    @EncryptAnnotation(encode = true)
    @DecryptAnnotation(decode = true)
    // @RequiresPermissions("power/power-department")
    public JsonResponse<?> queryAll() {
        return JsonResponse.success("SUCCESS", rbacOrganizationService.queryAll());
    }

    @ApiOperation(value = "添加部门接口(encode = false，decode = true)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/add")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = true)
    // @RequiresPermissions("power/power-department")
    public JsonResponse<?> add(@RequestBody RbacOrganizationParam rbacOrganizationParam) {
        ParamValidatorUtil.validate(rbacOrganizationParam);
        rbacOrganizationService.add(rbacOrganizationParam);
        return JsonResponse.success();
    }

    @ApiOperation(value = "修改部门名称接口(encode = false，decode = true)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/updateName")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = true)
    // @RequiresPermissions("power/power-department")
    public JsonResponse<?> updateName(@RequestBody RbacOrganizationParam rbacOrganizationParam) {
        ParamValidatorUtil.validate(rbacOrganizationParam);
        rbacOrganizationService.updateName(rbacOrganizationParam);
        return JsonResponse.success();
    }

    @ApiOperation(value = "删除部门接口(不操作)", httpMethod = "GET", notes = "")
    @GetMapping(path = "/delete")
    // @RequiresPermissions("power/power-department")
    public JsonResponse<?> delete(@RequestParam Long id) {
        rbacOrganizationService.delete(id);
        return JsonResponse.success();
    }
}
