package com.ihrdata.demo.service;

import com.ihrdata.demo.web.param.AccountParamByAccount;
import com.ihrdata.demo.web.param.AccountParamByPhone;
import com.ihrdata.demo.web.pojo.VerifyCodePojo;
import com.ihrdata.demo.web.vo.RbacUserVo;

/**
 * 账户service
 *
 * @Author wangwz
 * @Date 2020/11/24
 */
public interface AccountService {

    /**
     * 存储验证码信息
     */
    VerifyCodePojo cacheRedis();

    /**
     * 账号登录
     */
    RbacUserVo loginByAccount(AccountParamByAccount param);

    /**
     * 手机号登录
     */
    RbacUserVo loginByPhone(AccountParamByPhone param);

    /**
     * 修改密码
     */
    void changePassword(String oldPass, String newPass,String token);

    /**
     * 登出
     */
    void logout(String token);
}
