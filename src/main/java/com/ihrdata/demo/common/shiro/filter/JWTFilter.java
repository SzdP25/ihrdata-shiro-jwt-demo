package com.ihrdata.demo.common.shiro.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ihrdata.demo.common.jedis.RedisPool;
import com.ihrdata.wtool.common.exception.NeedLoginException;
import com.ihrdata.wtool.common.jsonrespone.JsonResponse;
import com.ihrdata.wtool.common.jsonrespone.JsonResponseEnum;
import com.ihrdata.wtool.common.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * JWTFilter class
 *
 * @author wangwz
 * @date 2021/5/31
 */
@Component
public class JWTFilter extends BasicHttpAuthenticationFilter implements Filter {

    /**
     * Redis token存储位置
     */
    private static String TOKEN_LOCAL = "LOGIN_TOKEN:";

    /**
     * 若带有 token，则对 token 进行检查，否则直接通过
     *
     * @Author wangwz
     * @Date 2021/5/31
     * @Params @param request:
     * @param response:
     * @param mappedValue:
     * @return boolean
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws
        UnauthorizedException {
        // 判断请求的请求头是否含有token
        if (isLoginAttempt(request, response)) {
            // 存在，则进入 executeLogin 方法执行登入，检查 token 是否正确
            try {
                executeLogin(request, response);
                return true;
            } catch (Exception e) {
                //token 错误
                e.getMessage();
            }
        }
        // 如不存在 token，提示Need_login
        return false;
    }

    /**
     * 判断用户是否想要登录。
     * 检测 header 里面是否包含 token 字段
     *
     * @Author wangwz
     * @Date 2021/5/31
     * @Params @param request:
     * @param response:
     * @return boolean
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest)request;
        String token = req.getHeader("token");
        return token != null;
    }

    /**
     * 执行登录操作（校验Token）
     *
     * @Author wangwz
     * @Date 2021/5/31
     * @Params @param request:
     * @param response:
     * @return boolean
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        // getSpringRedisTemplate();
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String token = httpServletRequest.getHeader("token");

        // 校验Token是否在有效期内
        Jedis jedis = null;
        String isToken = null;
        try {
            jedis = RedisPool.getResource();
            isToken = jedis.get(TOKEN_LOCAL + token);
            RedisPool.close(jedis);
        } catch (Exception ex) {
            RedisPool.close(jedis);
        }

        // 不存在抛出未登录异常
        if (StringUtils.isEmpty(isToken)) {
            throw new NeedLoginException();
        }
        return true;
    }

    /**
     * 解决跨域
     *
     * @Author wangwz
     * @Date 2021/6/2
     * @Params @param request:
     * @param response:
     * @return boolean
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;

        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        // 解决一下跨域问题
        httpServletResponse
            .setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
        httpServletResponse.setHeader("Access-Control-Max-Age", "0");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
            "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma,Last-Modified,Cache-Control,Expires,Content-Type,X-E4M-With,userId,token");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("XDomainRequestAllowed", "1");
        httpServletResponse.getWriter()
            .write(JsonUtil.castToString(JsonResponse.needLogin(JsonResponseEnum.NEED_LOGIN.getRemark())));
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
        return false;
    }
}
