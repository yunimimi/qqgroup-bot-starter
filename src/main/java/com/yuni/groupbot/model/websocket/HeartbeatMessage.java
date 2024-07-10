package com.yuni.groupbot.model.websocket;

import lombok.Data;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 上午11:34
 */
@Data
public class HeartbeatMessage {
    private Integer op = 1;
    private Integer d;

    public HeartbeatMessage(Integer d) {
        this.d = d;
    }
}
