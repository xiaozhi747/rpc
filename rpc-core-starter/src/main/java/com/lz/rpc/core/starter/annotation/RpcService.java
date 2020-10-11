package com.lz.rpc.core.starter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /*
     * value属性用来标记这个服务的实现类对应的接口，
     * RPC框架中服务提供者和消费者之间会共同引用一个服务接口的包，
     * 当我们需要远程调用的时候实际上只需要调用接口中定义的方法即可。
     */
    Class<?> value();

}
