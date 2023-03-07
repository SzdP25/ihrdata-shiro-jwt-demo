package com.ihrdata.demo.web.param;

import java.util.List;

import com.ihrdata.wtool.web.param.PagingParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ApiModel(value = "权限角色参数对象")
public class RbacRolePagingParam extends PagingParam {
    @Length(max = 100, message = "角色名称长度最大为100位")
    @ApiModelProperty(value = "角色名")
    private String roleName;

    @ApiModelProperty(value = "资源Id集合")
    private List<Long> resourceId;
}
