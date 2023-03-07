package com.ihrdata.demo.generator.mybatis;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

/**
 * 代码生成器
 *
 * @Author wangwz
 * @Date 2020/11/23
 * @Params @param null:
 * @return
 */
@Slf4j
public class Generator {

    /**
     * 生成代码
     *
     * @throws Exception
     */
    @Test
    public void generatorCode() throws Exception {
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config =
            cp.parseConfiguration(Generator.class.getResourceAsStream("/static/generatorConfig.xml"));
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
        for (String warning : warnings) {
            log.info("WARNING:{}", warning);
        }
    }
}