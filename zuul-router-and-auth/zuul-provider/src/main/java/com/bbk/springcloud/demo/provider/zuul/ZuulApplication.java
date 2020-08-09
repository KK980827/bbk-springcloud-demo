package com.bbk.springcloud.demo.provider.zuul;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableZuulProxy
public class ZuulApplication implements ApplicationRunner {

    @Value(value = "${server.port:8080}")
    private Integer port;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ZuulApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("------------------------------------");
        System.out.println("zuul running... port = " + port);
        System.out.println("------------------------------------");
    }
}
