package com.bbk.springcloud.demo.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteCall implements IRemoteCall{

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String now() {
        // url中的是，服务提供方在Eureka注册的应用名称。请求时候Ribbon会根据服务名称判断url。
        String url = "http://EUREKA-PROVIDER-TIME/now";
        return restTemplate.getForObject(url, String.class);
    }

}
