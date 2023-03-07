package com.ihrdata.demo.common.shiro.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ShiroUser implements Serializable {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private Long orgId;
    private Long userId;
}
