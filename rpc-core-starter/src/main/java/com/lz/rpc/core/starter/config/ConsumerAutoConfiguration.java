package com.lz.rpc.core.starter.config;

import com.lz.rpc.core.starter.annotation.RpcConsumer;
import com.lz.rpc.core.starter.consumer.RpcProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 17:29
 */
@Configuration
@ConditionalOnClass(RpcConsumer.class)
@EnableConfigurationProperties(RpcProperties.class)
public class ConsumerAutoConfiguration {

    @Autowired
    private RpcProxy rpcProxy;

    /**
     * 通过反射将带有 @RpcConsumer 注解的类设置一个 RpcProxy 代理
     * @return0
     */
    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                Class<?> objClz = bean.getClass();
                for (Field field : objClz.getDeclaredFields()) {
                    RpcConsumer rpcConsumer = field.getAnnotation(RpcConsumer.class);
                    if (rpcConsumer != null) {
                        Class<?> type = field.getType();
                        field.setAccessible(true);
                        try {
                            field.set(bean, rpcProxy.create(type, rpcConsumer.providerName()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } finally {
                            field.setAccessible(false);
                        }
                    }
                }
                return bean;
            }
        };
    }
}
