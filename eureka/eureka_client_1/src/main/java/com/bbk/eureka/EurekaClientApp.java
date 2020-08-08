package com.bbk.eureka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Getter
@RestController
@SpringBootApplication
//开启Eureka Client服务，
@EnableEurekaClient
public class EurekaClientApp implements ApplicationRunner {

    @Value(value = "${server.port:8080}")
    private Integer port;
    @Value(value = "${spring.profiles.active:default}")
    private String active;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EurekaClientApp.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    @GetMapping("/hi")
    public String hi() {
        return "我是主动注册到Eureka服务中心，并对外提供web服务的eureka_client_1, active = " + active;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("------------------------------------");
        System.out.println("client running... active profiles = " + active);
        System.out.println("------------------------------------");
    }
}
