package com.bob.cdms;

import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 启动类
 * @author zengxuan
 */
@SpringBootApplication
@ServletComponentScan(basePackages = "com.bob.cdms.filter")
@MapperScan("com.bob.cdms.mapper")
@EnableMPP
public class CdmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CdmsApplication.class, args);
    }

}
