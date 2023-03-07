package com.ihrdata.demo.common.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWTShiroToken
 *
 * @Author wangwz
 * @Date 2021/5/31
 * @Params @param null:
 * @return
 */
public class JWTShiroToken implements AuthenticationToken {
    private String token;

    public JWTShiroToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}