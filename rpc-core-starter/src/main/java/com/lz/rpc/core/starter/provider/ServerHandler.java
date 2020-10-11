package com.lz.rpc.core.starter.provider;

import com.lz.rpc.core.starter.model.RpcRequest;
import com.lz.rpc.core.starter.model.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 20:13
 */
@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcRequest request) throws Exception {
        log.info("provider accept request,{}", request);
        // 定义返回的对象
        RpcResponse rpcResponse = RpcResponse.builder()
                .requestId(request.getRequestId())
                .result(handle(request))
                .build();

        // 将返回结果塞入 channelHandlerContext
        channelHandlerContext.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Exception {
        String className = request.getClassName();
        Class<?> clazz = Class.forName(className);
        Object o = BeanFactory.getBean(clazz);
        // 获取调用的方法名称
        String methodName = request.getMethodName();
        // 获取参数类型
        Class<?>[] paramTypes = request.getParamTypes();
        // 获取具体参数
        Object[] params = request.getParams();
        // 调用实现类的制定方法并返回结果
        Method method = clazz.getMethod(methodName, paramTypes);
        Object result = method.invoke(o, params);
        return result;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty provider caught error,", cause);
        ctx.close();
    }

}
