package com.bbk.springcloud.demo.customer.sum;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Feign熔断处理类，必须实现FallBackFactory<Feign接口>。
 * 并且通过Factory拿到异常对象之后，还必须通过某种方式（这里使用new()），
 * 传入一个Feign熔断处理类。保证调用方的正常响应。
 */
@Component
public class FallBackFactory implements FallbackFactory<IRemoteCall> {

    @Override
    public IRemoteCall create(Throwable cause) {
        // 拿到throwable, 进行后续处理。
        System.out.println("----------模拟异常处理--------");
        if (cause instanceof FeignException) {
            final int i = ((FeignException) cause).status();
            System.out.println("远程调用响应状态码:" + i);
        }else {
            System.out.println(cause.toString());
        }
        System.out.println("----------------------------------");
        return new RemoteCall();
    }

}
