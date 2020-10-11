package com.lz.rpc.core.starter.model;

import lombok.*;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 18:06
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
public class RpcResponse {

    private String requestId;

    private Throwable error;

    // 服务端返回的调用对象，实际的服务提供者
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public RpcResponse(String requestId, Throwable error, Object result) {
        this.requestId = requestId;
        this.error = error;
        this.result = result;
    }
}
