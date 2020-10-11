package com.lz.rpc.provider.service.impl;

import com.lz.rpc.api.HelloRpcService;
import com.lz.rpc.core.starter.annotation.RpcService;

/**
 * @author linzhi
 * @date Created in 2020/10/11 22:21
 */
@RpcService(HelloRpcService.class)
public class HelloRpcServiceImpl implements HelloRpcService {

    @Override
    public String sayHello() {
        return "Hello RPC!";
    }

}
