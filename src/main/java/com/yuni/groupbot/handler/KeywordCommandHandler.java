package com.yuni.groupbot.handler;

import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.model.BotEventContext;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;

import java.util.Set;

/**
 * @author zhuangwenqiang
 * @date 2024/7/15 上午8:56
 */
public interface KeywordCommandHandler extends BotEventHandler {

    @Override
    void handle(BotEventContext context);

    @Override
    Set<BotEvent> subscribe();

    String keyword();

    String description();

    String example();

    String instructions();



    @Override
    default boolean match(BotEventContext context) {
        String content = context.getContent();
        if (content != null && content.startsWith("/" + keyword())) {
            context.setContent(content.replace("/" + keyword(), ""));
            return true;
        }
        return false;
    }
}
