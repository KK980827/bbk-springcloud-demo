package com.bbk.springcloud.demo.customer.sum;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 使用@FeignClients注解，指定远程服务的名称。Feign将指定寻找该服务。
 * 指定在远程调用失败的时候，要匹配执行的类。注意该类要加入IOC。
 */

//1.使用指定的熔断类处理。但是无法获取异常信息
//@FeignClient(name = "EUREKA-PROVIDER-SUM", fallback = RemoteCall.class)

//使用fallBackFactory处理，可以拿到异常对象。
@FeignClient(value = "EUREKA-PROVIDER-SUM", fallbackFactory = FallBackFactory.class)
public interface IRemoteCall {

    @GetMapping("/sum")
    String sum(@RequestParam(name = "a") Integer sumA, @RequestParam(name = "b") Integer sumB);

}
