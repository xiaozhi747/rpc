package com.lz.rpc.core.starter.config;

import com.lz.rpc.core.starter.consumer.RpcProxy;
import com.lz.rpc.core.starter.exception.ZkConnectException;
import com.lz.rpc.core.starter.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author linzhi
 * @date Created in 2020/10/10 23:08
 * 将 ServiceDiscovery 和 RpcProxy 加入 IOC 容器
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcAutoConfiguration {

    @Autowired
    private RpcProperties rpcProperties;

    @Bean
    @ConditionalOnMissingBean(ServiceDiscovery.class)
    public ServiceDiscovery serviceDiscovery() {
        ServiceDiscovery serviceDiscovery = null;
        try {
            serviceDiscovery = new ServiceDiscovery(rpcProperties.getRegisterAddress());
        } catch (ZkConnectException e) {
            log.error("zk connect failed:", e.getMessage(), e.getCause());
            e.printStackTrace();
        }
        return serviceDiscovery;
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcProxy rpcProxy() {
        RpcProxy rpcProxy = new RpcProxy();
        rpcProxy.setServiceDiscovery(serviceDiscovery());
        return rpcProxy;
    }
}
