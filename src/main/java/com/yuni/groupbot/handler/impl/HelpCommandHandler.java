package com.yuni.groupbot.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.handler.KeywordCommandHandler;
import com.yuni.groupbot.model.BotEventContext;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhuangwenqiang
 * @date 2024/7/15 上午9:02
 */
public class HelpCommandHandler implements KeywordCommandHandler {

    @Override
    public void handle(BotEventContext context) {

        List<KeywordCommandHandler> handlers = new ArrayList<>();
        handlers.add(this);
        for (String string : context.getBotProperties().getEventHandlers()) {
            BotEventHandler handler = SpringUtil.getBean(string, BotEventHandler.class);
            if (handler instanceof KeywordCommandHandler) {
                handlers.add((KeywordCommandHandler) handler);
            }
        }
        StringBuilder template = new StringBuilder("\r\n" +
                "当前可用命令列表：\r\n");
        for (KeywordCommandHandler handler : handlers) {
            template.append(StrUtil.format("/{}  \r\n" +
                    "    功能：【{}】\r\n" +
                    "    示例：【{}】\r\n", handler.keyword(), handler.description(), handler.example()));
            if (StrUtil.isNotBlank(handler.instructions())) {
                template.append(StrUtil.format("    说明：【{}】\r\n", handler.instructions()));
            }
        }
        context.setReply(template.toString());
    }

    @Override
    public Set<BotEvent> subscribe() {
        return CollectionUtil.newHashSet(BotEvent.GROUP_AT_MESSAGE_CREATE);
    }

    @Override
    public String keyword() {
        return "help";
    }

    @Override
    public String description() {
        return "获取帮助信息";
    }

    @Override
    public String example() {
        return "/help";
    }


    @Override
    public String instructions() {
        return "";
    }
}
