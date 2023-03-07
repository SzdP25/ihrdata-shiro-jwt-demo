package com.ihrdata.demo.common.shiro.crypto;

import com.ihrdata.wtool.common.utils.PropertiesUtil;
import org.apache.shiro.crypto.hash.SimpleHash;

public class ShiroCrypto {
    public static String encryptPassword(String password) {
        return new SimpleHash(PropertiesUtil.getProperty("shiro.hash-algorithm-name"), password,
            PropertiesUtil.getProperty("shiro.salt"),
            Integer.valueOf(PropertiesUtil.getProperty("shiro.hash-iterations"))).toHex();
    }
}
