package com.ihrdata.demo.web.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ApiModel(value = "全部部门对象", description = "全部部门对象")
public class RbacOrganizationVo implements Comparable<RbacOrganizationVo> {
    @ApiModelProperty(value = "部门编码")
    private String organizationCode;

    @ApiModelProperty(value = "部门名称")
    private String organizationName;

    @ApiModelProperty(value = "部门Id")
    private Long organizationId;

    @ApiModelProperty(value = "父级Id")
    private Long parentId;

    @ApiModelProperty(value = "部门区域")
    private String organizationArea;

    @ApiModelProperty(value = "部门序号")
    private Integer seq;

    @ApiModelProperty(value = "子级部门集合")
    private List<RbacOrganizationVo> children;

    @Override
    public int compareTo(RbacOrganizationVo o) {
        //先按照年龄排序
        int i = Integer.valueOf(this.getSeq()) - Integer.valueOf(o.getSeq());
        return i;
    }
}
