package com.ihrdata.demo.web.param;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ApiModel(value = "权限用户修改参数对象")
public class RbacUserUpdateParam {
    @NotNull(message = "ID不能为空")
    @Min(value = 1, message = "ID最小为1")
    private Long id;

    @NotBlank(message = "员工姓名不能为空")
    private String nickname;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 1, max = 100, message = "用户名长度为1~100位")
    private String username;

    @NotBlank(message = "用户名不能为空")
    private String userPassword;

    private long roleId;

    @NotNull(message = "用户状态不能为空")
    private byte userStatus;

}
