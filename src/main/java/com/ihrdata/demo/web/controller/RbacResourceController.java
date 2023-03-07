package com.ihrdata.demo.web.controller;

import java.util.Map;

import com.ihrdata.demo.service.RbacResourceService;
import com.ihrdata.wtool.common.annotation.DecryptAnnotation;
import com.ihrdata.wtool.common.annotation.EncryptAnnotation;
import com.ihrdata.wtool.common.jsonrespone.JsonResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Api(value = "权限模块-资源管理", tags = {"权限模块资源管理"})
@RequestMapping(path = "/rbac/resource")
public class RbacResourceController {
    @Autowired
    private RbacResourceService rbacResourceService;

    @ApiOperation(value = "查询全部资源接口(encode = true，decode = false)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/query")
    @EncryptAnnotation(encode = true)
    // @DecryptAnnotation(decode = false)
    // @RequiresPermissions("test")
    public JsonResponse<?> query() {
        // 返回全部资源
        return JsonResponse.success("查询成功", rbacResourceService.list());
    }

    @ApiOperation(value = "修改资源名称接口(encode = false，decode = false)", httpMethod = "GET", notes = "")
    @GetMapping(path = "/updateResourceName")
    // @EncryptAnnotation(encode = false)
    // @DecryptAnnotation(decode = false)
    // @RequiresPermissions("test")
    public JsonResponse<?> updateResourceName(@RequestParam Long id, @RequestParam String resourceName) {
        rbacResourceService.updateResourceName(id, resourceName);
        return JsonResponse.success();
    }

    @ApiOperation(value = "修改资源序号接口(encode = false，decode = false)", httpMethod = "POST", notes = "")
    @PostMapping(path = "/updatateSeq")
    @EncryptAnnotation(encode = false)
    @DecryptAnnotation(decode = false)
    // @RequiresPermissions("test")
    public JsonResponse<?> updatateSeq(@RequestBody Map<Long, Integer> paramMap) {
        rbacResourceService.updatateSeq(paramMap);
        return JsonResponse.success();
    }

}
