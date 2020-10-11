package com.lz.rpc.core.starter.config;

import com.lz.rpc.core.starter.annotation.RpcService;
import com.lz.rpc.core.starter.common.RpcDecoder;
import com.lz.rpc.core.starter.common.RpcEncoder;
import com.lz.rpc.core.starter.model.RpcRequest;
import com.lz.rpc.core.starter.model.RpcResponse;
import com.lz.rpc.core.starter.provider.BeanFactory;
import com.lz.rpc.core.starter.provider.ServerHandler;
import com.lz.rpc.core.starter.registry.RegistryServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

@Configuration
// 只有在classpath下能找到RpcService类才会构建这个bean。
@ConditionalOnClass(RpcService.class)
public class ProviderAutoConfiguration {

    private Logger logger = LoggerFactory.getLogger(ProviderAutoConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RpcProperties rpcProperties;

    @PostConstruct
    public void  init() {
        logger.info("rpc server start scanning provider service...");
        Map<String, Object> beanMap = this.applicationContext.getBeansWithAnnotation(RpcService.class);
        if (beanMap != null && !beanMap.isEmpty()) {
            beanMap.entrySet().forEach(kv -> {
                initProviderBean(kv.getKey(), kv.getValue());
            });
            logger.info("rpc server scan over...");
            // 如果有服务的话才启动 netty server
            startNetty(rpcProperties.getPort());
        }

    }

    /**
     * 将服务类交由自定义的BeanFactory管理
     * @param beanName
     * @param bean
     */
    private void initProviderBean(String beanName, Object bean) {
        RpcService rpcService = this.applicationContext.findAnnotationOnBean(beanName, RpcService.class);
        BeanFactory.addBean(rpcService.value(), bean);
    }

    /**
     * 启动netty server
     * @param port
     *          netty启动的端口
     */
    public void startNetty(int port) {
        EventLoopGroup boos = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(boos, worker).channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new ServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            logger.info("server started on port : {}", port);
            // netty服务端启动成功后，向zk注册这个服务
            logger.info("rpcProperties.getRegisterAddress()" + rpcProperties.getRegisterAddress());
            new RegistryServer(rpcProperties.getRegisterAddress(),
                    rpcProperties.getTimeout(), rpcProperties.getServerName(),
                    rpcProperties.getHost(), port)
                    .register();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            boos.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
