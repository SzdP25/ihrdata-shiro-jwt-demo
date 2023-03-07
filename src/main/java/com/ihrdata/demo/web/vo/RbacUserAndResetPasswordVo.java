package com.ihrdata.demo.web.vo;

import java.util.List;

import com.ihrdata.demo.model.RbacRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RbacUserAndResetPasswordVo {
    @ApiModelProperty(value = "初始密码")
    private String resetPassword;

    @ApiModelProperty(value = "全部角色")
    private List<RbacRole> byAll;
}
