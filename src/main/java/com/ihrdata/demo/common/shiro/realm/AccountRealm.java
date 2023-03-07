package com.ihrdata.demo.common.shiro.realm;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.ihrdata.demo.common.shiro.dto.ShiroUser;
import com.ihrdata.demo.model.RbacResource;
import com.ihrdata.demo.model.RbacRole;
import com.ihrdata.demo.model.RbacUser;
import com.ihrdata.demo.service.RbacResourceService;
import com.ihrdata.demo.service.RbacRoleService;
import com.ihrdata.demo.service.RbacUserService;
import com.ihrdata.wtool.common.utils.PropertiesUtil;
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
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountRealm extends AuthorizingRealm {
    @Autowired
    private RbacUserService rbacUserService;

    @Autowired
    private RbacRoleService rbacRoleService;

    @Autowired
    private RbacResourceService rbacResourceService;

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

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        List<RbacUser> rbacUserList = rbacUserService.getByUserName(token.getPrincipal().toString());

        if (CollectionUtils.isEmpty(rbacUserList)) {
            return null;
        }

        RbacUser rbacUser = rbacUserList.get(0);

        if (rbacUser.getUserStatus() != 1) {
            return null;
        }

        ShiroUser shiroUser =
            ShiroUser.builder().id(rbacUser.getId()).username(rbacUser.getUsername()).nickname(rbacUser.getNickname())
                .userId(rbacUser.getId()).build();

        SimpleAuthenticationInfo authentication =
            new SimpleAuthenticationInfo(shiroUser, rbacUser.getPassword(), "realm");
        authentication.setCredentialsSalt(ByteSource.Util.bytes(PropertiesUtil.getProperty("shiro.salt")));

        return authentication;
    }

}
