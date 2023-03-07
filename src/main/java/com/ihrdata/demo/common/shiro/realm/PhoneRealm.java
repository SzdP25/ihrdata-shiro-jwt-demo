package com.ihrdata.demo.common.shiro.realm;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.ihrdata.demo.common.jwt.JWTUtils;
import com.ihrdata.demo.common.shiro.dto.ShiroUser;
import com.ihrdata.demo.common.shiro.token.JWTShiroToken;
import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.model.RbacRole;
import com.ihrdata.demo.model.RbacUser;
import com.ihrdata.demo.service.RbacResourceService;
import com.ihrdata.demo.service.RbacRoleService;
import com.ihrdata.demo.service.RbacUserService;
import com.ihrdata.wtool.common.exception.BusinessException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * PhoneRealm class
 *
 * @author wangwz
 * @date 2021/5/31
 */
public class PhoneRealm extends AuthorizingRealm {
    @Autowired
    private RbacUserService rbacUserService;

    @Autowired
    private RbacRoleService rbacRoleService;

    @Autowired
    private RbacResourceService rbacResourceService;

    /**
     * 授权
     *
     * @Author wangwz
     * @Date 2021/5/31
     * @Params @param principals:
     * @return org.apache.shiro.authz.AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        ShiroUser shiroUser = (ShiroUser)principals.getPrimaryPrincipal();

        List<RbacRole> rbacRoleList = rbacRoleService.getByUserId(shiroUser.getId());

        Iterator<RbacRole> iterator = rbacRoleList.iterator();
        while (iterator.hasNext()) {
            RbacRole rbacRole = iterator.next();
            if (rbacRole.getRoleStatus() != 1) {
                iterator.remove();
            }
        }

        if (CollectionUtils.isEmpty(rbacRoleList)) {
            return new SimpleAuthorizationInfo();
        }

        SimpleAuthorizationInfo authorization = new SimpleAuthorizationInfo();

        Set<String> roleSet = Sets.newHashSet();
        Set<String> resourceSet = Sets.newHashSet();
        for (RbacRole rbacRole : rbacRoleList) {
            roleSet.add(rbacRole.getRoleName());

            List<RbacResource> rbacResourceList = rbacResourceService.getByRoleId(rbacRole.getId());
            if (CollectionUtils.isNotEmpty(rbacResourceList)) {
                for (RbacResource rbacResource : rbacResourceList) {
                    if (rbacResource.getResourceStatus() == 1
                        && StringUtils.isNotBlank(rbacResource.getResourceUrl())) {
                        resourceSet.add(rbacResource.getResourceUrl());
                    }
                }
            }
        }

        authorization.setRoles(roleSet);
        authorization.setStringPermissions(resourceSet);

        return authorization;
    }

    /**
     * 认证
     *
     * @Author wangwz
     * @Date 2021/5/31
     * @Params @param token:
     * @return org.apache.shiro.authc.AuthenticationInfo
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        JWTShiroToken jwtShiroToken = null;
        // 如果是JWTShiroToken，则强转，获取token信息；否则不处理。
        if (authenticationToken instanceof JWTShiroToken) {
            jwtShiroToken = (JWTShiroToken)authenticationToken;
        } else {
            return null;
        }
        String jwtToken = (String)jwtShiroToken.getPrincipal();

        // 解析Token
        analysisJWTToken(jwtToken);
        String userId = JWTUtils.getTokenByKey(jwtToken, "userid");

        RbacUser rbacUser = rbacUserService.getByUserId(userId);

        if (null == rbacUser) {
            return null;
        }

        if (rbacUser.getUserStatus() != 1) {
            return null;
        }

        ShiroUser shiroUser =
            ShiroUser.builder().id(rbacUser.getId()).username(rbacUser.getUsername()).nickname(rbacUser.getNickname())
                .userId(rbacUser.getId()).phone(rbacUser.getPhone()).build();

        SimpleAuthenticationInfo authentication =
            new SimpleAuthenticationInfo(shiroUser, jwtToken, "tokenRealm");

        return authentication;
    }

    @Override
    public boolean supports(AuthenticationToken var1) {
        return var1 instanceof JWTShiroToken;
    }

    /**
     * 解析JWTtoken
     *
     * @Author wangwz
     * @Date 2021/6/2
     * @Params @param jwtToken:
     * @return java.lang.String
     */
    private void analysisJWTToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)) {
            throw new BusinessException("Token异常，请重新登录");
        }
        // 校验Token是否合法
        boolean checkToken = JWTUtils.checkToken(jwtToken);
        if (!checkToken) {
            throw new BusinessException("非法Token，请重新登录");
        }
    }
}
