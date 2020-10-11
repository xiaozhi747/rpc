package com.lz.rpc.consumer.controller;

import com.lz.rpc.api.HelloRpcService;
import com.lz.rpc.core.starter.annotation.RpcConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author linzhi
 * @date Created in 2020/10/11 22:40
 */
@RestController
@RequestMapping("/hello-rpc")
public class HelloRpcController {


    @RpcConsumer(providerName = "provider")
    private HelloRpcService helloRpcService;

    @GetMapping("/hello")
    public String hello() {
        return helloRpcService.sayHello();
    }
}