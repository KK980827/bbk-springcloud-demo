# Zuul实现路由转发以及权限认证。
> 本例模拟以下情境：
- 存在两个服务：hello / bye，二者均注册到EurekaServer。
- 搭建Zuul服务，注册到EurekaServer。
- 用户所有请求不再直接达到hello/bye, 全部到达Zuul并进行路由转发，转发规则：
所有以hello-api开头的请求，转发到hello服务，所有以bye-api开头的请求，转发到bye服务。
- Zuul进行权限认证，在尝试访问Hello服务的时候，会认证身份，如果请求者未携带参数：auth=123, 
则不通过Zuul的身份验证。

# EurekaServer

EurekaServer环境可以直接使用eureka-launch-singleton-custer工程以单节点启动即可。


# 搭建Provider
- 编写两个服务提供方，Hello在访问的时候，sayHello。bye在访问的时候，sayByeBye。
- Hello/Bye是作为两个不同的服务，一起注册到Eureka里面的。注意application.name以及hostname配置独立。
- 搭建过程略过。

# 引入Zuul
- Zuul对于EurekaServer来说，其实也是作为一个Client的，所以他也需要作为客户端
注册到EurekaServer
- Zuul需要拿到其他在EurekaServer注册的客户端信息，从而来实现路由等操作。
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```
- 开启使用Zuul:
```java
@EnableZuulProxy
public class ZuulApplication implements ApplicationRunner {}
```
- 配置yaml，使zuul注册到EurekaServer，并设置路径映射：
```yaml
zuul:
  routes:
    hello-api:
      # 配置路由规则，所以以hello-api开头的请求，全部是针对服务 EUREKA-PROVIDER-HELLO
      path: /hello-api/**
      serviceId: EUREKA-PROVIDER-HELLO
    bye-api:
      path: /bye-api/**
      serviceId: EUREKA-PROVIDER-BYE
```

# 网关身份验证
- 编写Filter即可，继承至ZuulFilter，实现其中的一些方法即可。可以参见本例AuthFailter，
如果要查看网关身份的验证效果。
- 开启权限验证，只要在AuthFilter上打上@Commpoments注解将其注入到IOC即可。
- 此时访问hello-api的时候，需要带上参数auth=123才可以通过验证。

# 测试
- 依次启动EurekaServer / HelloProvider / ByeProvider / Zuul
- 进入EurekaServer，查看以上服务是否注册成功。
- 试着直接访问HelloProvider/ByeProvider,测试这些提供业务的接口是否正常。
    - Hello：http://sc.kk.com:9001/say
    - Bye：http://sc.kk.com:9002/say
- 通过网关访问：
    - Hello：http://zuul0.kk.com:12000/hello-api/say
    - Bye：http://zuul0.kk.com:12000/bye-api/say

在通过网关进行访问时候，用户并不会直接接触到内部服务地址，完全由暴露在外面的网关进行负责。


# Tips:
- com.netflix.zuul.exception.ZuulException: Forwarding error:
    - Zuul转发到对应服务的时候，服务所在的地址eureka.instance.hostname可能无效，比如这里就是bye-provider
    注册到eurekaServer的时候，提供的hostname是provider2.kk.com, 这个地址在本地电脑的hosts文件并没有配置，
    所以即便Zuul通过路径映射，在EurekaServer找到了对应的服务，但是获取到这个地址的hostname是无效的。