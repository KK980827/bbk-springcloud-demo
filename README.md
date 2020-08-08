# EurekaServer配置

## Tips:
- 配置其他Eureka实例地址的时候，eureka.client.serviceUrl, 此处serviceUrl必须使用驼峰法！使用service-url的话，检测不到，会继续使用默认的地址。
- 配置其他Eureka实例地址的时候，eureka.client.serviceUrl.defaultZone配置名称，必须一样。

## 单节点Eureka启动模式
- 直接启动类 EurekaServerApp即可，默认使用的application.yaml文件，将自己仅作为一个单独的Server启动，
不会主动向其他Server注册自己，也不会拉取客户端在其他Server注册的信息实例。
- 启动之后在 sc.kk.com:8000 可以看到Eureka的启动界面信息。
- 在该模式下，仅仅有一个Eureka实例，其他客户端想要注册到该实例的时候，就需要eureka.client.service-url.default-zone上配置的连接地址，
来连接到Eureka实例。

## 集群模式启动
- 启动EurekaServerApp，启动之前需要在IDEA配置允许并行启动，重复启动三次，且每一次都通过环境变量指定要使用的yaml配置文件。
- 启动第一次：配置参数-spring.profiles.active=s1，此时节点1启动，运行于8001端口，并尝试向另外两个节点(位于8002, 8003)注册。如果
另外的两个节点还没启动，那么节点1的注册行为就会失败。此时进入节点1的eureka界面，会发现：unavailable-replicas 一栏，显示了
不可用的服务地址。active
- 启动第二次，配置参数-spring.profiles.=s2，此时节点2启动，运行于8002端口，也会尝试向8001，8003注册，此时节点1与节点2的互相注册
是成功的，进入节点1，节点2的eureka界面，会发现available-replicas上，已经显示了各自成功注册的节点。但是都一样的，节点1，节点2都是提示无法
注册到节点3，因为还未启动。
- 启动第三次，配置参数-spring.profiles.active=s3，此时节点3启动，运行于8003端口，会尝试向8001，8002注册自己，同样的，节点1，节点2也会因为重试
在节点3成功启动之后，注册上来，此时进入这三个节点的eureka界面，会发现unavailable-replicas栏已经没有信息了，说明每个节点打算对外连接的
server都是成功的。
- 这样，Eureka集群就启动成功，每个节点都会注册到除自己之外的其他所有节点上，并互相同步其他Server上注册的客户端信息，如果一个Server挂掉，那么在
一定时间之后，其他Server会自动将其剔除掉。
- 在集群模式启动的时候，每个节点需要注意：
    1. application.name名称需要保持一致，这样在注册的时候，相同名称之间的应用，才会被认为是各自的副本，如果每个应用配置了不同的名称，
    那么即便可以正常启动，并配置了相互注册，那么每个节点的unavailable-replicas都会提示不可用，因为虽然有注册的地址，但是根据名称判断出，
    这不是一个针对当前节点的副本 ，自然也就会归为不可用副本。取而代之的是，每个应用作为【不同】的实例注册到每个Eureka上。
    2. 各节点在满足1的条件下，会被认为是一个应用的多副本集，然后具体区分是使用各自节点的eureka.instance.hostname进行区分开来的。这些节点在注册到Eureka的时候。
    会存在标识自己的hostname。
    3. 通过eureka.client.serviceUrl.defaultZone来指定要连接的【同一应用】下的某个具体节点的【hostname】地址，这样的话，虽然几个节点都是一个应用，但是由于
    各自都通过hostname唯一指定了要连接的某个副本，所以不会发生混乱。而如果某个节点(NodeA)的defaultZone配置要连接同应用下hostname=aa.com的副本，但是该hostname副本
    不存在，那么针对NodeA来说，aa.com这个副本就是不可用的。
    4. 综上阐述，在配置集群的时候，除了同应用设置同样的applicationname之外，还需要为各自配置正确的hostname, 并且确定要主动对外连接的地址，有真正对应hostname的副本存在。
    
## Others:
- 对于eureka/client来说，模拟作为一个服务提供方，对外提供服务。此时对于EurekaServer来说，本应用是一个主动过来注册的客户端。应用内引入了
Eureka-Client组件，然后将自己注册到Eureka-Server去。在注册的时候，eureka.client.serviceUrl.defaultZone 中指定的注册地址，必须能够对应
上EurekaServer的hostname, 如果链接的是集群，那么也至少要对应上集群中某个节点的名称。连接Server集群的时候，只需要配置连接到其中一个节点
的地址即可，在连接成功之后，本客户端的连接信息会自动在Eureka集群之间进行传播。

 - 无论是使用单节点还是集群模式，在正常启动之后，可以配置EurekaClient向Server进行注册（使用eureka/client即可。），注册成功之后，就可以
 看到对应Client的applicationName, 如果在集群模式下，注册的时候可以试着只指定一个集群节点进行注册，启动成功后，
 该客户端的信息将会自动在集群之间进行复制。需要注意Client在进行注册的时候，提供注册链接的hostname必须是集群中存在的。