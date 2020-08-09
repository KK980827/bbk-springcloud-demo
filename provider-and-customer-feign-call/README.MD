# 服务注册与调用(Feign)

> 启动Eureka单节点即可。

> 模拟一个应用提供两数相加服务，另一个服务通过feign进行调用。

# 服务提供方：

- 与provider-and-customer-robbon-call中，服务提供方的过程完全一致。
- 注意服务注册时候的名称。
- 注意要注册Eureka的地址是否正常。

# 服务消费方

- 消费方注意在注册到EurekaServer的时候，要开启拉取客户端实例信息，这样feign/Ribbon才可以根据虎
获取到的服务名匹配到url。
- 使用feign（一个声明式的伪Http客户端）进行服务调用，需要加入依赖：
```xml
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
```
- feign内部已经默认集成了Ribbon。
- 启用feign，启动类加入注解：@EnablefeignClients。
- 建立接口IRemoteCall，匹配要调用远程服务的请求接口，使用@feignClients注解，指定远程服务的名称。
feign将指定寻找该服务。指定在远程调用失败的时候，要匹配执行的类。注意该类要加入IOC。
```java

@FeignClient(name = "EUREKA-PROVIDER-SUM", fallback = RemoteCall.class)
public interface IRemoteCall {
    @GetMapping("/sum")
    String sum(@RequestParam(name = "a") Integer sumA, @RequestParam(name = "b") Integer sumB);
}

@Service
public class RemoteCall implements IRemoteCall{
    @Override
    public String sum(Integer sumA, Integer sumB) {
        return "远程服务不可用";
    }
}

```
- Feign默认未开启熔断，配置开启：
```yaml
feign:
  hystrix:
    enabled: true
```

# 测试：
- 启动EurekaServer，启动Sum-Provider并注册到EurekaServer。
- 启动Sum-Provider，注册到EurekaServer。
- 直接访问服务提供者：http://sc.kk.com:9001/sum?a=1&b=100，返回结果101，说明提供者服务可用。
- 访问服务消费者：http://sc.kk.com:10001/sum/1/1, 它将在内部通过Feign调用服务提供者，获取结果。

# Tips
- 服务提供者同样可以提供多个实例注册到EurekaServer里面。可以验证Feigen内部的负载均衡服务。
- 停止服务提供者，继续访问服务消费者，此时Feign将调用失败，避免线程一致阻塞。
    - 如果此时Feign配置了使用熔断类【@FeignClient(name = "EUREKA-PROVIDER-SUM", fallback = RemoteCall.class)】会返回：服务消费者调用其他提供方, 返回信息：Feign远程调用失败，服务不可用达到熔断效果，
    - 如果此时Feign配置了使用熔断工厂类【@FeignClient(name = "EUREKA-PROVIDER-SUM", fallbackFactory = FallBackFactory.class)】，除了会响应错误外，还会在控制台输出异常信息的相关信息.

# RollBackFactory
Feign默认提供的熔断回退类rollBackClass仅仅针对调用失败时候，如何保证对请求用户继续返回一个结果。保证了【客户端-服务消费者】之间服务的可用，即时是一种失败回退策略。
而针对【服务消费者-服务提供者】二者之间调用为何失败，失败信息等，则是无法在这里直接获取到的，为了更全面的获取到信息，可以使用rollbackFactory配置：

- 注意：实现工厂类的时候，对外需要返回一个熔断处理类，以保证客户端可以继续使用熔断的方式处理请求。
- 【这个熔断处理类不要使用构造注入，@Authwire注入，应该直接new()】否则会Feign
将会一直提示远程调用异常。一直执行熔断方法且执行不会成功.
```java
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
        } else {
            System.out.println(cause.getCause());
        }
        System.out.println("----------------------------------");
        // 不要利用Spring注入。
        return new RemoteCall();
    }
}

```