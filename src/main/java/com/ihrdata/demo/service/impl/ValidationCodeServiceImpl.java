package com.ihrdata.demo.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ihrdata.demo.common.utils.ValidationCodeUtil;
import com.ihrdata.demo.service.ValidationCodeService;
import com.ihrdata.wtool.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * ValidationCodeServiceImpl class
 *
 * @author wangwz
 * @date 2021/5/25
 */
@Service("validationCodeService")
@Slf4j
public class ValidationCodeServiceImpl implements ValidationCodeService {
    /**
     * 身份认证地址
     */
    @Value("${mas_url}")
    String url;
    /**
     * 用户登录帐号
     */
    @Value("${mas_userAccount}")
    String userAccount;
    /**
     * 用户登录密码
     */
    @Value("${mas_password}")
    String password;
    /**
     * 用户企业名称
     */
    @Value("${mas_ecname}")
    String ecname;
    /**
     * 扩展码
     */
    @Value("${mas_addSerial}")
    String addSerial;
    /**
     * 优先级
     */
    @Value("${mas_smsPriority}")
    int smsPriority;
    /**
     * 网关签名编码
     */
    @Value("${mas_sign}")
    String sign;
    /**
     * 是否需要上行
     */
    @Value("${mas_isMo}")
    boolean isMo;
    /**
     * 短信模版id
     */
    @Value("${mas_templateId}")
    String templateId;
    /**
     * 验证码有效时间
     */
    @Value("${code_effective}")
    int codeEffective;
    /**
     * 验证码重发间隔时间
     */
    @Value("${code_resend}")
    int codeResend;
    /**
     * 验证码使用次数限制
     */
    @Value("${code_num}")
    int codeNum;
    /**
     * 验证码使用时间限制
     */
    @Value("${code_time}")
    int codeTime;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取验证码
     *
     * @Author wangwz
     * @Date 2021/5/25
     * @Params @param phone:手机号
     * @return void
     * @param phone
     */
    @Override
    public void validationCode(String phone) {
        String validationCode;
        // 判断是否发送该验证码短信
        boolean isSend = true;
        int leftSecond;
        // 验证验证码使用次数
        int redisCodeNum = 0;
        String num = (String)redisTemplate.opsForValue().get(phone + "_smsloginNum");
        if (StringUtils.isNotBlank(num)) {
            redisCodeNum = Integer.valueOf(num);
            // 判断是否超过限制次数
            if (redisCodeNum < codeNum) {
                // 判断是否缓存该账号验证码
                boolean isExist;
                String redisCode;
                String redisLeft;
                redisCode = (String)redisTemplate.opsForValue()
                    .get(phone + "_smsloginUser");
                redisLeft = (String)redisTemplate.opsForValue()
                    .get(phone + "_smsleftUser");

                isExist = StringUtils.isNotBlank(redisLeft);
                if (isExist) {
                    // 从redis取出验证码
                    validationCode = redisCode;
                    leftSecond = redisTemplate.getExpire(phone + "_smsleftUser")
                        .intValue();
                    isSend = false;
                } else {
                    redisCodeNum++;
                    // 获取redis剩余失效时间
                    int codeSurplusTime = redisTemplate.getExpire(phone + "_smsloginNum").intValue();
                    if (codeSurplusTime > 0) {
                        // redis更新使用次数
                        redisTemplate.opsForValue().set(phone + "_smsloginNum", String.valueOf(redisCodeNum),
                            codeSurplusTime, TimeUnit.SECONDS);
                    }
                    // 没找到该账号的验证码，新生成验证码
                    validationCode = ValidationCodeUtil.getValidationCode6();
                    // 缓存验证码并设置超时时间
                    redisTemplate.opsForValue().set(phone + "_smsloginUser",
                        validationCode, codeEffective, TimeUnit.SECONDS);
                    // 缓存验证码发送间隔时间
                    redisTemplate.opsForValue().set(phone + "_smsleftUser", "-1",
                        codeResend, TimeUnit.SECONDS);
                    leftSecond = codeResend;
                }
            } else {
                // 获取redis剩余失效时间
                int codeSurplusTime = redisTemplate.getExpire(phone + "_smsloginNum").intValue();
                throw new BusinessException("验证码获取次数超出限制，请等待" + codeSurplusTime + "秒后再尝试获取！");
            }
        } else {
            // 判断是否缓存该账号验证码
            boolean isExist;
            String redisCode;
            String redisLeft;
            redisCode = (String)redisTemplate.opsForValue()
                .get(phone + "_smsloginUser");
            redisLeft = (String)redisTemplate.opsForValue().get(phone + "_smsleftUser");
            isExist = StringUtils.isNotBlank(redisLeft);
            if (isExist) {
                // 从redis取出验证码
                validationCode = redisCode;
                leftSecond = redisTemplate.getExpire(phone + "_smsleftUser").intValue();
                isSend = false;
            } else {
                // redis记录使用次数
                redisTemplate.opsForValue().set(phone + "_smsloginNum", String.valueOf(redisCodeNum + 1),
                    codeTime, TimeUnit.SECONDS);
                // 没找到该账号的验证码，新生成验证码
                validationCode = ValidationCodeUtil.getValidationCode6();
                // 缓存验证码并设置超时时间
                redisTemplate.opsForValue().set(phone + "_smsloginUser", validationCode,
                    codeEffective, TimeUnit.SECONDS);
                // 缓存验证码发送间隔时间
                redisTemplate.opsForValue().set(phone + "_smsleftUser", "-1", codeResend,
                    TimeUnit.SECONDS);
                leftSecond = codeResend;
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("validationCode", validationCode);
        map.put("isSend", isSend);
        map.put("leftSecond", leftSecond);
        log.info("验证码【" + validationCode + "】");

        int sendResult = 0;
        if (isSend) {
            String[] mobiles = {phone};
            if (!ValidationCodeUtil.getIsLoggedin()) {
                ValidationCodeUtil.initParams(url, userAccount, password, ecname, addSerial, smsPriority, sign, isMo,
                    templateId);
            }
            String[] params = {validationCode};
            sendResult = ValidationCodeUtil.sendSms(mobiles, params);

            if (sendResult == 1) {
                log.info("验证码信息发送成功{}", map);
            } else {
                log.info("{}验证码信息发送失败{}", phone, sendResult);
                throw new BusinessException("");
            }
        } else {
            throw new BusinessException("验证码信息在有效期内不再重新发送");
        }
    }
}
