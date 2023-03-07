package com.ihrdata.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@Slf4j
@SpringBootApplication
@MapperScan(basePackages = "com.ihrdata.demo.dao")
public class IhrdataShiroJwtDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(IhrdataShiroJwtDemoApplication.class, args);
    }

}
