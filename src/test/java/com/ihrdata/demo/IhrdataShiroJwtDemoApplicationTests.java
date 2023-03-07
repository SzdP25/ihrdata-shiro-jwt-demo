package com.ihrdata.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import com.alibaba.excel.EasyExcel;
import com.google.common.collect.Maps;
import com.ihrdata.demo.common.jwt.JWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IhrdataShiroJwtDemoApplicationTests {

    @Test
    public void test() throws FileNotFoundException {
        HashMap<String, String> map = Maps.newHashMap();
        map.put("phone", "13130817688");
        String jwt = JWTUtils.creatJWT(map);
        System.out.println("token:" + jwt);
        String phone = JWTUtils.getTokenByKey(jwt, "phone");
        System.out.println(phone);
    }

}
