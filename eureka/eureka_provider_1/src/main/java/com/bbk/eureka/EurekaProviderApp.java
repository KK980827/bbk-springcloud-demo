package com.bbk.eureka;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Getter
@RestController
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
public class EurekaProviderApp implements ApplicationRunner {

    @Value(value = "${server.port:8080}")
    private Integer port;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EurekaProviderApp.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    /**
     * 模拟应用对外提供服务。
     * @return 当前时间
     */
    @GetMapping("/now")
    public String hi() {
        return "我是来自端口" + port + "的服务提供方，我提供的结果：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }


    @Override
    public void run(ApplicationArguments args) {
        System.out.println("------------------------------------");
        System.out.println("time provider running... port = " + port);
        System.out.println("------------------------------------");
    }
}
