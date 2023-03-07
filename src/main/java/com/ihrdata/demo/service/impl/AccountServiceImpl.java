package com.ihrdata.demo.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;
import com.ihrdata.demo.common.jwt.JWTUtils;
import com.ihrdata.demo.common.shiro.crypto.ShiroCrypto;
import com.ihrdata.demo.common.shiro.dto.ShiroUser;
import com.ihrdata.demo.common.utils.VerifyUtil;
import com.ihrdata.demo.dao.RbacUserMapper;
import com.ihrdata.demo.model.RbacUser;
import com.ihrdata.demo.service.AccountService;
import com.ihrdata.demo.service.RbacUserService;
import com.ihrdata.demo.web.param.AccountParamByAccount;
import com.ihrdata.demo.web.param.AccountParamByPhone;
import com.ihrdata.demo.web.pojo.VerifyCodePojo;
import com.ihrdata.demo.web.vo.RbacUserVo;
import com.ihrdata.wtool.common.enums.DataStatusEnum;
import com.ihrdata.wtool.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

    // 超时时间3min
    private static int CAPTCHA_EXPIRES = 3 * 60;

    /**
     * 登录有效天数
     */
    @Value("${server.default.valid-day}")
    private Long validDay;
    /**
     * Redis token存储位置
     */
    private static String TOKEN_LOCAL = "LOGIN_TOKEN:";

    @Autowired
    private RbacUserService rbacUserService;

    @Autowired
    private RbacUserMapper rbacUserMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 存储验证码信息
     */
    @Override
    public VerifyCodePojo cacheRedis() {
        VerifyCodePojo captcha = VerifyUtil.getCaptcha();
        //将验证码以<key,value>形式缓存到redis
        redisTemplate.opsForValue().set(captcha.getUuid(), captcha.getVerifyCode(), CAPTCHA_EXPIRES, TimeUnit.SECONDS);
        return captcha;
    }

    /**
     * 账号登录
     * @param param
     * @return
     */
    @Override
    public RbacUserVo loginByAccount(AccountParamByAccount param) {
        // // 检验验证码
        // boolean hasKey = redisTemplate.hasKey(param.getUuid());
        // if (!hasKey) {
        //     throw new BusinessException("重新刷新验证码");
        // }
        //
        // String captchaCode = redisTemplate.opsForValue().get(param.getUuid());
        // redisTemplate.delete(param.getUuid());
        // if (!(captchaCode != null && param.getVerify() != null && captchaCode.equalsIgnoreCase(param.getVerify()))) {
        //     throw new BusinessException("验证码错误!");
        // }

        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(param.getUsername(),
            ShiroCrypto.encryptPassword(param.getPassword()));
        try {
            subject.login(usernamePasswordToken);
        } catch (Exception e) {
            throw new BusinessException("用户名或密码错误");
        }
        long userId = ((ShiroUser)SecurityUtils.getSubject().getPrincipal()).getId();
        // 生成Token
        HashMap<String, String> map = Maps.newHashMap();
        map.put("userid",String.valueOf(userId));
        String jwt = JWTUtils.creatJWT(map);
        // 存储Token
        redisTemplate.opsForValue().set(TOKEN_LOCAL + jwt, String.valueOf(userId), validDay, TimeUnit.DAYS);

        RbacUserVo rbacUserVo = rbacUserService.get(userId);
        rbacUserVo.setToken(jwt);
        return rbacUserVo;
    }

    /**
     * 手机号登录
     * @param param
     * @return
     */
    @Override
    public RbacUserVo loginByPhone(AccountParamByPhone param) {
        // 校验手机号
        Example exampleByPhone = new Example(RbacUser.class);
        exampleByPhone.createCriteria()
            .andEqualTo("phone", param.getPhone())
            .andEqualTo("isDelete", DataStatusEnum.NOTDELETE.getCode());
        RbacUser rbacUser = rbacUserMapper.selectOneByExample(exampleByPhone);

        if (null == rbacUser) {
            throw new BusinessException("手机号不存在，请先注册");
        }
        //校验验证码
        if (!redisTemplate.opsForValue().get(param.getPhone() + "_smsloginUser").equals(param.getSmsVerify())) {
            throw new BusinessException("验证码不正确");
        }
        // 生成Token
        HashMap<String, String> map = Maps.newHashMap();
        map.put("userid",rbacUser.getId().toString());
        String jwt = JWTUtils.creatJWT(map);

        // 存储Token
        redisTemplate.opsForValue().set(TOKEN_LOCAL + jwt, rbacUser.getId().toString(), validDay, TimeUnit.DAYS);

        RbacUserVo rbacUserVo = rbacUserService.get(rbacUser.getId());
        rbacUserVo.setToken(jwt);
        return rbacUserVo;
    }

    @Override
    public void changePassword(String oldPass, String newPass,String token) {
        Long userId = Long.valueOf(JWTUtils.getTokenByKey(token, "userid"));

        if (oldPass.equals(newPass)) {
            throw new BusinessException("新密码不能与旧密码重复");
        }

        RbacUser rbacUser = rbacUserMapper.selectByPrimaryKey(userId);

        if (!StringUtils.equals(ShiroCrypto.encryptPassword(oldPass), rbacUser.getPassword())) {
            throw new BusinessException("旧密码错误");
        }

        rbacUser.setPassword(ShiroCrypto.encryptPassword(newPass));
        rbacUser.setUpdateUser(userId);
        rbacUser.setGmtModified(new Date());

        rbacUserMapper.updateByPrimaryKeySelective(rbacUser);
    }

    @Override
    public void logout(String token) {
        SecurityUtils.getSubject().logout();
        // 删除Token
        redisTemplate.delete(token);
    }

    /*******************************私有方法********************************/

}
