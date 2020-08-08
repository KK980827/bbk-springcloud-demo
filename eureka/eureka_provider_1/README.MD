- 模拟作为一个服务提供方，对外提供服务。
- 此时对于EurekaServer来说，本应用是一个主动过来注册的客户端。
- 应用内引入了Eureka-Client组件，然后将自己注册到Eureka-Server去。
- 在注册的时候，eureka.client.serviceUrl.defaultZone 中指定的注册地址，必须能够对应上EurekaServer的hostname, 如果链接的是集群，
那么也至少要对应上集群中某个节点的名称。连接Server集群的时候，只需要配置连接到其中一个节点的地址即可，在连接
成功之后，本客户端的连接信息会自动在Eureka集群之间进行传播。
