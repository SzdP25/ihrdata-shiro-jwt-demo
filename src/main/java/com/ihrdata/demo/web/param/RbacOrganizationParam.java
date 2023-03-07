package com.ihrdata.demo.web.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "部门参数对象", description = "部门机构参数对象")
public class RbacOrganizationParam {
    @NotBlank(message = "部门名称不能为空")
    @ApiModelProperty(value = "部门名称")
    private String organizationName;

    @NotNull(message = "父级Id不能为空")
    @ApiModelProperty(value = "父级Id")
    private Long parentId;

    @ApiModelProperty(value = "部门Id")
    private Long id;

    @ApiModelProperty(value = "部门序号")
    private Integer seq;
}
