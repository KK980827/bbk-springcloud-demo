# 服务提供与调用(ribbon + RestTemplate)

在Eureka注册中心启动的情况下：

> 模拟一个简单的调用过程：
- 一个服务提供者provider注册到注册中心。
- 一个服务消费者customer注册到注册中心。
- consumer通过注册中心，通过Ribbon+RsetTemplate调用provider提供的服务。


## 服务注册：
参见eureka_provider_1下实现，大概流程：
- 引入eureka_client包
- 使用@EnableEurekaClient作为Eureka客户端自动装配。
- 使用@EnableDiscoveryClient允许服务发现，注册到EurekaServer中。
- 编写对外提供的业务（获取时间）。

在完成上面的步骤之后，启动服务提供方，将会自动注册到EurekaServer中。后续其他服务消费者可以调用。

## 服务消费：
参见eureka_customer_1下实现，大概流程：
- 引入eureka_client / ribbon 包。
- 使用@EnableEurekaClient作为Eureka客户端自动装配。
- 使用@EnableDiscoveryClient允许服务发现，注册到EurekaServer中。
- 注入ResTemplate对象，并使用@LoadBalance开启负载均衡。
- 使用RestTemplate实现一个远程调用的接口，ttp://EUREK-APROVIDER-TIME/now，其中EUREK-APROVIDER-TIME
为服务提供方注册在EurekaServer中的名称。后面是该改服务提供方的API接口信息。直接向该接口发起请求，经过
Ribbon之后，会自动检索出该服务名称对应的url。进行请求。

## 使用：
依次启动Server/Provider/Customer, 调用Customer中的接口，Ribbon根据从Eureka获取到的客户端信息，
自动寻找EUREKA-PROVIDER-TIME服务对应的url地址，如果这个服务对应多个实例，那么就会采取一定负载均衡策略。
依次请求到不同的url上面。

## 负载均衡实现：
- 启动Server。
- 分别配置不同端口启动两次Provider，则该服务会有两个实例在EurekaServer。
- 启动一次Customer。

在通过Customer进行请求的时候，外部是通过服务名进行请求的，但是经过Ribbon处理之后，会发现该服务
对应了两个具体的实例，Ribbon就会根据相关的策略，轮询/随机请求这两个URL。

## Tips:
调用出现错误：
- Request URI does not contain a valid hostname:
    - 不要使用下划线作为服务名称。    
 - No instances available for eureka-provider-time
    - 判断Provider/Customer是否真的已经注册到了Eureka.
    - 判断Customer是否被允许从Eureka获取客户端信息:eureka.client.fetch-registry:true. 如果未false, 那么即便
    Customer注册到了Eureka，但是也只能作为一个对外提供服务方，因为他不被允许获取服务的注册信息。
    自然在发起请求的时候，Ribbon获取获取不到任何实例。
    