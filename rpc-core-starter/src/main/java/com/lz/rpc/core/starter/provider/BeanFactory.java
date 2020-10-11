package com.lz.rpc.core.starter.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 16:35
 */
public class BeanFactory {

    /**
     * 持有当前Rpc服务提供的所有的服务（即有@RpcService注解的类），
     * 服务端接受请求时，会从该map里找到处理请求的类，并调用相关方法
     *
     */
    private static Map<Class<?>, Object> beans = new HashMap<>();

    private BeanFactory() {
    }

    public static void addBean(Class<?> interfaceClass, Object bean) {
        beans.put(interfaceClass, bean);
    }

    public static Object getBean(Class<?> interfaceClass) {
        return beans.getOrDefault(interfaceClass, null);
    }

}
