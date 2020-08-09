# ConfigServer配置中心

## 建立配置中心
虽然配置中心也分为Server/Client，但是对于EurekaServer来说，他们提供服务的Client。不过是具体的应用中，
划分出来了ConfigClient，去ConfigServer里面取配置信息，而不是去EurekaServer。

- 引入ConfigServer:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```
- 启用ConfigServer
```java
@EnableConfigServer
public class ConfigServerApplication {}
```
- 设置ConfigServer要连接的Git信息：
```yaml
spring:
  application:
    name: git-config-server
  cloud:
    config:
      # 仓库分支
      label: master
      server:
        git:
          # 配置中心要连接的gir地址,注意是URI, 不是URL
          uri: https://github.com/KK980827/bbk-springcloud-demo-config.git
          # git仓库下具体的文件夹路径。设置之后，将默认在该文件夹下处理配置信息。
          searchPaths: demo
          # 如果git仓库私有的，则需要设置账户密码。
          username:
          password:
```

# 测试：

> 无需注册到EurekaServer

> 在git仓库的demo文件夹下，存在文件application-c1.yaml, 里面的内容为：
```yaml
test-key: test-value
```
> 在git仓库的demo文件夹下，存在文件application-c2.yaml, 里面的内容为：
```yaml
test-key: I'm KK
```

- 启动ConfigServer，运行于7000端口，可以通过以下映射方式获取git中的值, 其中后缀.properties/.yaml/.yml均可。
    - /{application}/{profile}[/{label}]
    -  /{application}-{profile}.yml
    - /{label}/{application}-{profile}.yml
    - /{application}-{profile}.properties
    - /{label}/{application}-{profile}.properties
    - label指的是分支名称。
    - application指的是文件名称前半部分：application-c1.yaml, 指的就是application
    - profile指的是文件名称的后半部分：application-c1.yaml, 指的就是c1.
- 可以根据映射规则请求值，比如：http://sc.kk.com:7000/application-c1.yaml将响应值：test-key: test-value

## 建立ConfigClient
建立ConfigClient，去ConfigServer里面取配置信息

- 引入依赖：
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```
-  为ConfigClient指定要连接ConfigServer的信息, 在bootstrap.yaml
```yaml
spring:
  cloud:
    config:
      label: master
      # application-c1.yaml
      profile: c1
      uri: http://config-server0.kk.com:7000
```
- 在ConfigClient尝试注入值:
```java
@Value("${test-key}")
private String key;

@GetMapping("/test-key")
public String getKey() {
    return key;
}
```
如果ConfigClient的bootstrap.yaml配置profile为c1, 那么会去application-c1.yaml获取值：
此时访问http://config-client0.kk.com:13000/test-key将会正常值: test-value.。而如果配置的是c2, 则会返回I'm KK

# Tips:
- 启动Config-Client的时候，提示还是从localhost:8888尝试连接ConfigServer。
    - 将连接ConfigServer的信息配置在bootstrap.yaml里面，该文件中配置的优先级更高。覆盖掉默认的配置。
- 启动的时候，如果配置了eureka-client包，那么会自动尝试向eureka-server进行注册，如果此时未配置eureka-server
信息，那么会启动报错。可以根据实际情况，如果需要注册到eurekaServer，就正确配置信息，否则去除eureka-client包即可。
