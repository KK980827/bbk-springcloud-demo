package com.bbk.eureka;

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
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Getter
@RestController
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class EurekaCustomerApp implements ApplicationRunner {

    @Value(value = "${server.port:8080}")
    private Integer port;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EurekaCustomerApp.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("------------------------------------");
        System.out.println("time customer running... port = " + port);
        System.out.println("------------------------------------");
    }

    /**
     * 借助Restemplate进行服务调用，并提供负载均衡功能。
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory(){{
            setReadTimeout(50000);
            setConnectTimeout(30000);
        }};
        return new RestTemplate(factory);
    }


    @Autowired
    private IRemoteCall remoteCall;

    /**
     * 模拟应用对外提供服务。
     * @return 当前时间
     */
    @GetMapping("/now")
    public String hi() {
        return "服务消费者调用其他提供方, 返回信息：" + remoteCall.now();
    }


}
