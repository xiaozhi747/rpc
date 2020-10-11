package com.lz.rpc.core.starter.registry;

import com.lz.rpc.core.starter.exception.ZkConnectException;
import com.lz.rpc.core.starter.model.ProviderInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 17:39
 */
// 由 lombok 自动生成该类的 log 静态常量，要打日志就可以直接打，不用再手动 new log 静态常量了
@Slf4j
public class ServiceDiscovery {

    private volatile List<ProviderInfo> dataList = new ArrayList<>();

    public ServiceDiscovery(String registoryAddress) throws ZkConnectException {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(registoryAddress, 2000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    log.info("consumer connect zk success!");
                }
            });
            watchNode(zooKeeper);
        } catch (Exception e) {
                throw new ZkConnectException("connect to zk exception," + e.getMessage(), e.getCause());
        }

    }

    private void watchNode(final ZooKeeper zk) {
        try {
            List<String> nodeList = zk.getChildren("/rpc", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // 如果有节点改变 或 有服务上线或下线，则重新获取节点信息
                    if (event.getType().equals(Event.EventType.NodeChildrenChanged)) {
                        watchNode(zk);
                    }
                }
            });
            List<ProviderInfo> providerInfos = new ArrayList<>();
            // 遍历子节点，获取服务名称
            for (String node: nodeList) {
                byte[] bytes = zk.getData("/rpc/" + node, false, null);
                String[] providerInfo = new String(bytes).split(",");
                if (providerInfo.length == 2) {
                    providerInfos.add(new ProviderInfo(providerInfo[0], providerInfo[1]));
                }
            }
            this.dataList = providerInfos;
            log.info("获取服务端列表成功：{}", this.dataList);
        } catch (Exception e) {
            log.error("watch error,", e);
        }
    }

    /**
     * 随机获取一个服务提供者（负载均衡）
     * @param providerName
     * @return
     */
    public ProviderInfo discover(String providerName) {
        if (dataList.isEmpty()) {
            return null;
        }
        List<ProviderInfo> providerInfos = dataList.stream().filter(data -> providerName.equals(data.getName()))
                .collect(Collectors.toList());
        if (providerInfos.isEmpty()) {
            return null;
        }
        return providerInfos.get(ThreadLocalRandom.current().nextInt(providerInfos.size()));
    }
}
