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
@Table(name = "sys_log")
public class SysLog {
    /**
     * 日志ID
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作人员Id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 用户名称
     */
    @Column(name = "username")
    private String username;

    /**
     * 用户操作
     */
    @Column(name = "user_operation")
    private String userOperation;

    /**
     * 用户操作类别
     */
    @Column(name = "operation_type")
    private String operationType;

    /**
     * 请求类
     */
    @Column(name = "req_clazz")
    private String reqClazz;

    /**
     * 请求方法
     */
    @Column(name = "req_method")
    private String reqMethod;

    /**
     * 请求参数名称
     */
    @Column(name = "req_params_name")
    private String reqParamsName;

    /**
     * 请求参数值
     */
    @Column(name = "req_params_value")
    private String reqParamsValue;

    /**
     * 请求IP
     */
    @Column(name = "req_ip")
    private String reqIp;

    /**
     * 执行时长
     */
    @Column(name = "cost_time")
    private Long costTime;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;
}