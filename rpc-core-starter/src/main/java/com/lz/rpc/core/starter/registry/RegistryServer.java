package com.lz.rpc.core.starter.registry;

import com.lz.rpc.core.starter.common.Constants;
import com.lz.rpc.core.starter.exception.ZkConnectException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 17:06
 */
public class RegistryServer {

    private Logger logger = LoggerFactory.getLogger(RegistryServer.class);

    /**
     * zk的地址
     */
    private String addr;

    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 服务名
     */
    private String serverName;

    private String host;

    private int port;

    public RegistryServer(String addr,
                          int timeout,
                          String serverName,
                          String host,
                          int port) {
        this.addr = addr;
        this.timeout = timeout;
        this.serverName = serverName;
        this.host = host;
        this.port = port;
    }

    /**
     * zk注册
     * @throws IOException
     * @throws InterruptedException
     */
    public void register() throws ZkConnectException {
        try {
            // 获取 zk 连接
            ZooKeeper zooKeeper = new ZooKeeper(addr, timeout, event -> {
                logger.info("registry zk connect success...");
            });
            if (zooKeeper.exists(Constants.ZK_ROOT_DIR, false) == null) {
                zooKeeper.create(Constants.ZK_ROOT_DIR, Constants.ZK_ROOT_DIR.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
            }
            zooKeeper.create(Constants.ZK_ROOT_DIR + "/" + serverName,
                    (serverName + "," + host + ":" + port).getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            logger.info("provider register success {}", serverName);
        } catch (Exception e) {
            throw new ZkConnectException("register to zk exception," + e.getMessage(), e.getCause());
        }
    }

}
