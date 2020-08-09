package com.bbk.springcloud.demo.customer.sum;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Getter
@RestController
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class SumCustomerApplication implements ApplicationRunner {

    @Value(value = "${server.port:8080}")
    private Integer port;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SumCustomerApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("------------------------------------");
        System.out.println("sum customer running... port = " + port);
        System.out.println("------------------------------------");
    }

    @Autowired
    private IRemoteCall remoteCall;

    /**
     * 模拟应用对外提供服务。
     *
     * @return 当前时间
     */
    @GetMapping("/sum/{a}/{b}")
    public String hi(@PathVariable(name = "a") Integer a, @PathVariable(name = "b") Integer b) {
        return "服务消费者调用其他提供方, 返回信息：" + remoteCall.sum(a, b);
    }

}
