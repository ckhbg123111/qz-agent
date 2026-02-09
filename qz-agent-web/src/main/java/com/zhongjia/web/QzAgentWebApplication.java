package com.zhongjia.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.zhongjia")
@MapperScan("com.zhongjia.biz.mapper")
public class QzAgentWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(QzAgentWebApplication.class, args);
    }
}
