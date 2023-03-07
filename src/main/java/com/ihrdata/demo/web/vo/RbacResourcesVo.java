package com.ihrdata.demo.web.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "当前用户拥有权限视图对象")
public class RbacResourcesVo {
    @ApiModelProperty(value = "权限ID")
    private Long resourceId;

    @ApiModelProperty(value = "权限父级ID")
    private Long resourceParentId;

    @ApiModelProperty(value = "权限名称")
    private String resourceName;

    @ApiModelProperty(value = "权限Url")
    private String resourceUrl;

    @ApiModelProperty(value = "背景颜色")
    private String background;

    @ApiModelProperty(value = "显示名称")
    private String perms;

    @ApiModelProperty(value = "权限类型")
    private Byte resourceType;

    @ApiModelProperty(value = "序号")
    private Integer seq;

    @ApiModelProperty(value = "children")
    private List<String> children;
}
