package com.bbk.eureka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 使用@EnableEurekaServer开启相关服务，作为注册中心存在。
 */
@Slf4j
@Getter
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApp{

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EurekaServerApp.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

}
