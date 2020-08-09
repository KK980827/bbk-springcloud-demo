package com.bbk.springcloud.demo.customer.time;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteCall implements IRemoteCall{

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @HystrixCommand(fallbackMethod = "fallBackNow")
    public String now() {
        // url中的是，服务提供方在Eureka注册的应用名称。请求时候Ribbon会根据服务名称判断url。
        String url = "http://EUREKA-PROVIDER-TIME/now";
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * 远程调用失败的时候，执行的方法。方法签名必须与被调用方法一致。
     * @return
     */
    private String fallBackNow() {
        return "远程服务不可用";
    }

}
