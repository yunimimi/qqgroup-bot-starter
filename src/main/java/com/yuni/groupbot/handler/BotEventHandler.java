package com.yuni.groupbot.handler;

import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.model.BotEventContext;

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
     * @param context 接收到到消息
     * @return 需要发送的内容，返回空则不发送
     */
    void handle(BotEventContext context);

    /**
     * 订阅的事件
     */
    Set<BotEvent> subscribe();

    /**
     * 可自定义匹配逻辑
     */
    boolean match(BotEventContext context);

    /**
     * 自定义后处理逻辑
     */
    default void postProcessing(BotEventContext context) {
    }

}
