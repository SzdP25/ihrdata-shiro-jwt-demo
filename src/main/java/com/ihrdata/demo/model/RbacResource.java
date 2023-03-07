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
@Table(name = "rbac_resource")
public class RbacResource {
    /**
     * 资源ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 上级资源ID
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 层级
     */
    @Column(name = "level")
    private String level;

    /**
     * 资源名称
     */
    @Column(name = "resource_name")
    private String resourceName;

    /**
     * 资源类型：1-菜单，2-按钮，3-其他
     */
    @Column(name = "resource_type")
    private Byte resourceType;

    /**
     * 资源URL
     */
    @Column(name = "resource_url")
    private String resourceUrl;

    /**
     * 授权
     */
    @Column(name = "perms")
    private String perms;

    /**
     * 资源状态：1-启用，2-停用
     */
    @Column(name = "resource_status")
    private Byte resourceStatus;

    /**
     * 序号
     */
    @Column(name = "seq")
    private Integer seq;

    /**
     * 创建者
     */
    @Column(name = "create_user")
    private Long createUser;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改者
     */
    @Column(name = "update_user")
    private Long updateUser;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @Column(name = "is_delete")
    private Byte isDelete;
}