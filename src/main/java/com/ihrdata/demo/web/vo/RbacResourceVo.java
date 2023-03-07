package com.ihrdata.demo.web.vo;

import java.util.List;

import com.ihrdata.demo.model.RbacResource;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "全部权限及已有权限视图对象")
public class RbacResourceVo {
    @ApiModelProperty(value = "父级权限")
    private RbacResource rbacResource;

    @ApiModelProperty(value = "子级权限集合")
    private List<RbacResourceVo> childList;
}
