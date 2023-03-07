package com.ihrdata.demo.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(value = "Rbac用户姓名ID对象")
public class RbacUserQueryVo {
    @ApiModelProperty(value = "账户信息ID")
    private Long id;

    @ApiModelProperty(value = "员工姓名")
    private String nickname;
}
