package com.lz.rpc.core.starter.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 23:05
 */
@Getter
@Setter
@Builder
@ConfigurationProperties(prefix = "spring.rpc")
public class RpcProperties {

    /**
     * zk的地址
     */
    private String registerAddress = "server:2181";

    /**
     * rpc服务的端口
     */
    private int port = 21810;

    /**
     * 服务名称
     */
    private String serverName = "rpc";

    /**
     * 服务地址
     */
    private String host = "localhost";

    /**
     * 超时时间
     */
    private int timeout = 2000;
}
