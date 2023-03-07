package com.ihrdata.demo.common.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

/**
 * JwtUtils class
 *
 * @author wangwz
 * @date 2020/11/3
 */
@Component
public class JWTUtils {

    /**
     * 失效毫秒值(1天)
     */
    private static Long EXPIRE_TIME = 60 * 24 * 60 * 1000L;

    /**
     * Token私钥
     */
    private static final String TOKEN_SECRET = "wtooldemo";

    /**
     * 过期时间
     */
    private static Date expireDate = new Date(System.currentTimeMillis() + EXPIRE_TIME);

    /**
     * 生成Token
     *
     * @Author wangwz
     * @Date 2021/6/2
     * @Params @param map:map参数
     * @return java.lang.String
     */
    public static String creatJWT(@NotNull Map<String, String> map) {
        // 设置加密密钥及算法
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);

        // 设置Jwt Header
        HashMap<String, Object> headerMap = Maps.newHashMap();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        // 创建JWTBuilder,并添加值
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k, v) ->
            builder.withClaim(k, v)
        );
        // 将自定义信息及 Header生成JWT
        String jwtToken = builder.withHeader(headerMap)
            // 设置过期时间
            .withExpiresAt(expireDate).sign(algorithm);
        return jwtToken;
    }

    /**
     * 校验Token
     *
     * @Author wangwz
     * @Date 2020/11/3
     * @Params @param token:token
     * @return boolean
     */
    public static boolean checkToken(@NotNull String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /**
     * 获取Token注册信息
     *
     * @Author wangwz
     * @Date 2020/11/3
     * @Params @param token:token
     * @return io.jsonwebtoken.Claims
     */
    public static Map<String, Claim> getTokenClaims(@NotNull String token) {
        try {
            return JWT.decode(token).getClaims();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据key获取token中指定值
     *
     * @Author wangwz
     * @Date 2021/6/2
     * @Params @param token:token
     * @param key: key
     * @return java.lang.String
     */
    public static String getTokenByKey(@NotNull String token, @NotNull String key) {
        try {
            Map<String, Claim> claims = JWT.decode(token).getClaims();
            String value = claims.get(key).asString();
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}
