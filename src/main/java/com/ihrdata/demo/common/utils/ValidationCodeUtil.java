package com.ihrdata.demo.common.utils;

import java.util.Random;
import java.util.UUID;

import com.mascloud.sdkclient.Client;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationCodeUtil {
    private static Client client;
    private static boolean isLoggedin;

    //身份认证地址
    private static String url;
    //用户登录帐号
    private static String userAccount;
    //用户登录密码
    private static String password;
    //用户企业名称
    private static String ecname;
    //扩展码
    private static String addSerial;
    //优先级
    private static int smsPriority;
    //网关签名编码
    private static String sign;
    //是否需要上行
    private static boolean isMo;
    //短信模版id
    private static String templateId;

    /**
     * 获取4位随机验证码
     * @returncom.mascloud.sdkclient.Client
     */
    public static String getValidationCode4() {
        return String.valueOf((new Random().nextInt(8999) + 1000));
    }

    /**
     * 获取6位随机验证码
     * @return
     */
    public static String getValidationCode6() {
        return String.valueOf((new Random().nextInt(899999) + 100000));
    }

    /**
     * 获取短信平台登录状态
     * @return
     */
    public static boolean getIsLoggedin() {
        return isLoggedin;
    }

    /**
     * 初始化短信平台参数信息
     * @return
     */
    public static void initParams(String paramUrl, String paramUserAccount, String paramPassword, String paramEcname,
        String paramAddSerial, int paramSmsPriority, String paramSign, boolean paramIsMo, String paramTemplateId) {
        log.info("初始化短信平台参数信息");
        url = paramUrl;
        userAccount = paramUserAccount;
        password = paramPassword;
        ecname = paramEcname;
        addSerial = paramAddSerial;
        smsPriority = paramSmsPriority;
        sign = paramSign;
        isMo = paramIsMo;
        templateId = paramTemplateId;
    }

    /**
     * 初始化客户端
     * @return
     */
    private static boolean initClient() {
        log.info("初始化短信发送客户端");
        client = Client.getInstance();
        log.info("url=" + url + " userAccount=" + userAccount + " password=" + password + " ecname=" + ecname);
        //测试环境
        isLoggedin = client.login(url, userAccount, password, ecname);
        return isLoggedin;
    }

    /**
     * 发送验证码
     * @param mobiles
     * @param params
     * @return
     */
    public static int sendSms(String[] mobiles, String[] params) {
        if (isLoggedin) {
            log.info("云MAS is Login successed");
        } else {
            if (initClient()) {
                log.info("云MAS Login successed");
            } else {
                log.info("云MAS Login failed");
                return 0;
            }
        }

        //发送消息
        log.info("addSerial=" + addSerial + " smsPriority=" + smsPriority + " sign=" + sign + " isMo=" + isMo
            + " templateId=" + templateId);
        //int sendResult = client.sendDSMS(mobiles, message, addSerial, smsPriority, sign, UUID.randomUUID().toString(), isMo);
        //发送模版短信
        int sendResult = client
            .sendTSMS(mobiles, templateId, params, addSerial, smsPriority, sign, UUID.randomUUID().toString());
        log.info("云MAS推送结果: " + sendResult);

        if (sendResult == 1) {
            log.info("发送信息成功");
        } else {
            log.info("发送信息失败，重新连接云MAS");
            isLoggedin = false;
        }
        return sendResult;
    }
}
