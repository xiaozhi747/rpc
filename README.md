# 一个基于Netty+Spring+Zookeeper 的轻量级分布式RPC框架
**Feature：**  
1、使用 Netty 实现网络传输  
2、使用 Protostuff 实现序列化和反序列化  
3、使用 Zookeeper 管理相关服务地址信息  
4、集成 Spring 通过注解注册服务  
5、集成 Spring 通过注解进行服务消费  

**使用方式：**  
1、本地安装好zk，修改 provider 和 consumer 工程下配置文件的 spring.rpc.register-addres 值为zk地址
2、启动provider  
3、启动consumer  
4、访问 localhost:8080/hello-rpc/hello
