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
@Table(name = "rbac_role")
public class RbacRole {
    /**
     * 角色ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色名称
     */
    @Column(name = "role_name")
    private String roleName;

    /**
     * 角色状态：1-启用，2-停用
     */
    @Column(name = "role_status")
    private Byte roleStatus;

    /**
     * 序号
     */
    @Column(name = "seq")
    private Byte seq;

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