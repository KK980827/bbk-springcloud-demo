package com.bbk.springcloud.demo.customer.sum;

import org.springframework.stereotype.Service;

/**
 * 实现远程服务的接口，在远程服务不可用的时候，将默认执行当前服务下的实现类来达到熔断效果。
 */
@Service
public class RemoteCall implements IRemoteCall {

    @Override
    public String sum(Integer sumA, Integer sumB) {
        return "Feign远程调用失败，服务不可用";
    }

}
