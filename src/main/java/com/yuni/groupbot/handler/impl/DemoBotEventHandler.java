package com.yuni.groupbot.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 上午10:52
 */
@Component
public class DemoBotEventHandler implements BotEventHandler{


    @Override
    public String handle(BotWebSocketMessage message) {
        return "这是一个测试的事件处理器，当你@机器人时，无论你输入什么都会返回这条信息";
    }

    @Override
    public Set<BotEvent> subscribe() {
        return CollectionUtil.newHashSet(BotEvent.GROUP_AT_MESSAGE_CREATE );
    }
}
