package com.lz.rpc.core.starter.model;

import lombok.*;

/**
 * @author ：linzhi
 * @date ：Created in 2020/10/10 17:41
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
public class ProviderInfo {

    private String name;

    private String addr;

    public ProviderInfo(String name, String addr) {
        this.name = name;
        this.addr = addr;
    }
}
