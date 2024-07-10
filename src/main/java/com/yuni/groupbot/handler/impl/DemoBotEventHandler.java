package com.yuni.groupbot.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.Set;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 上午10:52
 */
@ConditionalOnMissingBean(BotEventHandler.class)
public class DemoBotEventHandler implements BotEventHandler{


    @Override
    public String handle(BotWebSocketMessage message) {
        return "";
    }

    @Override
    public Set<BotEvent> subscribe() {
        return CollectionUtil.newHashSet(BotEvent.AT_MESSAGE_CREATE);
    }
}
