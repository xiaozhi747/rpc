package com.lz.rpc.core.starter.consumer;

import com.lz.rpc.core.starter.common.RpcDecoder;
import com.lz.rpc.core.starter.common.RpcEncoder;
import com.lz.rpc.core.starter.model.RpcRequest;
import com.lz.rpc.core.starter.model.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 18:06
 */
@Slf4j
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {

    private String host;

    private int port;

    /**
     * 用来接收服务器端的返回的。
     */
    private RpcResponse response;

    private CompletableFuture<String> future;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcResponse send(RpcRequest request){
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    // 连接的超时时间
                    // 如果超过该时间或无法建立连接，则连接失败
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(RpcClient.this);
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().writeAndFlush(request).sync();
            future = new CompletableFuture<>();
            // 获取到服务端返回的结果后，会先调用 channelRead0()，使阻塞着的future继续运行下去
            // 通过CompletableFuture的get()方法阻塞当前线程，直到接收到调用结果（PS:我们在channelRead0方法中收到返回结果后会将其设置成完成状态）
            future.get();
            // 拿到返回值后关闭netty连接
            if (response != null) {
                // 关闭netty连接。
                channelFuture.channel().closeFuture().sync();
            }
            return response;
        } catch (Exception e) {
            log.error("client send msg error,", e);
            e.printStackTrace();
            return null;
        } finally {
            worker.shutdownGracefully();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcResponse rpcResponse) {
        log.info("client get request result,{}", rpcResponse);
        this.response = rpcResponse;
        // 设置future为完成状态
        future.complete("");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("netty client caught exception,", cause);
        ctx.close();
    }
}
