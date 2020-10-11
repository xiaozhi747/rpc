package com.lz.rpc.core.starter.model;

import lombok.*;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 17:32
 */
@Getter
@Setter
@Builder
@ToString
//@NoArgsConstructor
public class RpcRequest {

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] paramTypes;

    private Object[] params;

}
