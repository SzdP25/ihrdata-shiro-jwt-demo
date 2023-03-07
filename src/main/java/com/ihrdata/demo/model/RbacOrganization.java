package com.ihrdata.demo.model;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "rbac_organization")
public class RbacOrganization implements Comparable<RbacOrganization> {
    /**
     * 机构信息ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 机构编码
     */
    @Column(name = "organization_code")
    private String organizationCode;

    /**
     * 机构名称
     */
    @Column(name = "organization_name")
    private String organizationName;

    /**
     * 上级机构
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 机构所属区域
     */
    @Column(name = "organization_area")
    private String organizationArea;

    /**
     * 序号
     */
    @Column(name = "seq")
    private Integer seq;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 是否删除 0-未删除，1-已删除
     */
    @Column(name = "is_deleted")
    private Byte isDeleted;

    /**
     * 机构状态 1-启用，2-停用
     */
    @Column(name = "organization_status")
    private Byte organizationStatus;

    @Override
    public int compareTo(RbacOrganization o) {
        //先按照年龄排序
        int i = Integer.valueOf(this.getSeq()) - Integer.valueOf(o.getSeq());
        return i;
    }
}