package com.ihrdata.demo.common.shiro.bean;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ihrdata.demo.common.shiro.filter.JWTFilter;
import com.ihrdata.demo.common.shiro.realm.AccountRealm;
import com.ihrdata.demo.common.shiro.realm.PhoneRealm;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 自定义拦截器的注入
        shiroFilterFactoryBean.getFilters().put("authc", new JWTFilter());

        // 设置过滤器链路
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/swagger-ui/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v3/api-docs", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/META-INF/resources/**", "anon");
        filterChainDefinitionMap.put("/loginByAccount", "anon");
        filterChainDefinitionMap.put("/loginByPhone", "anon");
        filterChainDefinitionMap.put("/logout", "anon");
        filterChainDefinitionMap.put("/getVerify", "anon");
        filterChainDefinitionMap.put("/captcha/get", "anon");
        filterChainDefinitionMap.put("/captcha/check", "anon");
        filterChainDefinitionMap.put("/validationCode/getValidationCode", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        // filterChainDefinitionMap.put("/**", "anon");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        // 设置多Realm
        List<Realm> realms = new ArrayList<>();
        realms.add(phoneRealm());
        realms.add(accountRealm());
        defaultSecurityManager.setRealms(realms);

        // 关闭Session
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        return defaultSecurityManager;
    }

    /**
     * 自定义Jwt域
     *
     * @Author wangwz
     * @Date 2021/6/2
     * @Params @param :
     * @return com.ihrdata.demo.common.shiro.token.JWTShiroToken
     */
    @Bean
    public PhoneRealm phoneRealm() {
        PhoneRealm phoneRealm = new PhoneRealm();
        return phoneRealm;
    }

    /**
     * 用户名密码域
     *
     * @Author wangwz
     * @Date 2021/7/12
     * @Params @param :
     * @return com.ihrdata.demo.common.shiro.realm.ShiroRealm
     */
    @Bean
    public AccountRealm accountRealm() {
        AccountRealm accountRealm = new AccountRealm();
        return accountRealm;
    }

    /**
     * 开启Shiro注解的使用
     * @return AuthorizationAttributeSourceAdvisor对象
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor sourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        sourceAdvisor.setSecurityManager(securityManager());
        return sourceAdvisor;
    }
}
