package com.ihrdata.demo.web.param;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ApiModel(value = "权限角色保存参数对象")
public class RbacRoleAddParam {
    @Min(value = 1, message = "ID最小为1")
    @ApiModelProperty(value = "ID")
    private String id;

    @NotBlank(message = "角色名称不能为空")
    @Length(min = 1, max = 100, message = "角色名称长度为1~100位")
    @ApiModelProperty(value = "角色名", required = true)
    private String roleName;

    @NotNull(message = "资源ID不能为空")
    @ApiModelProperty(value = "资源ID", required = true)
    private List<String> resourceIds;

    @ApiModelProperty(value = "token")
    private String token;
}
