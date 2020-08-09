package com.bbk.springcloud.demo.config.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Slf4j
public class ConfigClientApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ConfigClientApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    @Value("${test-key}")
    private String key;

    @GetMapping("/test-key")
    public String getKey() {
        return key;
    }
}
