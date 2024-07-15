package com.yuni.groupbot.service;

import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.model.properties.BotProperties;
import com.yuni.groupbot.utils.MessageSender;
import com.yuni.groupbot.utils.RequestUtil;
import com.yuni.groupbot.utils.TokenUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhuangwenqiang
 * @date 2024/7/11 下午4:10
 */
@Data
@Slf4j
public class BotService {

    private BotProperties properties;

    private MessageSender messageSender;

    private RequestUtil requestUtil;

    private TokenUtil tokenUtil;

    private EventSubscribeService eventSubscribeService;

    private List<BotEventHandler> messageHandlerList;

    public BotService(BotProperties properties, List<BotEventHandler> messageHandlerList) {
        this.properties = properties;
        this.messageHandlerList = messageHandlerList;
        this.requestUtil = new RequestUtil(properties);
        this.messageSender = new MessageSender(requestUtil);
        this.tokenUtil = new TokenUtil(properties);
        this.eventSubscribeService = new EventSubscribeService(messageHandlerList, requestUtil, tokenUtil, messageSender, properties);
        eventSubscribeService.init();
        log.info("{}机器人初始化，handler：【{}】",properties.getName(),messageHandlerList);
    }
}
