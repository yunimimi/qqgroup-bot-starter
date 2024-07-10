package com.yuni.groupbot.handler;

import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;

import java.util.Set;

/**
 * 机器人事件处理器
 *
 * @author zhuangwenqiang
 * @date 2024/7/10 上午9:56
 */
public interface BotEventHandler {

    /**
     * 机器人事件处理逻辑
     *
     * @param message 接收到到消息
     * @return 需要发送的内容，返回空则不发送
     */
    String handle(BotWebSocketMessage message);

    /**
     * 订阅的事件
     */
    Set<BotEvent> subscribe();

    default void accept(BotWebSocketMessage message) {
        if (this.subscribe().contains(message.getT()) && this.match(message)) {
            String response = this.handle(message);
            sendResponseMessage(message, response);
            this.postProcessing(message);
        }
    }

    /**
     * 可自定义匹配逻辑
     */
    default boolean match(BotWebSocketMessage message) {
        return true;
    }

    /**
     * 发送响应消息
     */
    default void sendResponseMessage(BotWebSocketMessage message, String content) {

    }

    /**
     * 自定义后处理逻辑
     */
    default void postProcessing(BotWebSocketMessage message) {

    }

}