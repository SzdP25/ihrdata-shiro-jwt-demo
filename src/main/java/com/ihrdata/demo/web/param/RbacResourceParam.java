package com.ihrdata.demo.web.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "权限资源参数对象")
public class RbacResourceParam {
    @ApiModelProperty(value = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "资源ID")
    private Long id;

    @ApiModelProperty(value = "父级ID")
    private Long parentId = 0L;

    @ApiModelProperty(value = "资源名")
    private String resourceName;

    @ApiModelProperty(value = "资源类型")
    private Byte resourceType;

    @ApiModelProperty(value = "资源名URL")
    private String resourceUrl;

    @ApiModelProperty(value = "序号")
    private Integer seq;
}
