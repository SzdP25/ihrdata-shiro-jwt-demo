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
@Table(name = "rbac_user")
public class RbacUser {
    /**
     * 账户信息ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 昵称
     */
    @Column(name = "nickname")
    private String nickname;

    /**
     * 账户名称
     */
    @Column(name = "username")
    private String username;

    /**
     * 账户密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 手机号
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 用户状态：1-正常，2-注销，3-停用
     */
    @Column(name = "user_status")
    private Byte userStatus;

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