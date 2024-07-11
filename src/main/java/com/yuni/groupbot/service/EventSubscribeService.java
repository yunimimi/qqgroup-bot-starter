package com.yuni.groupbot.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.config.BotConfiguration;
import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.enums.BotEventGroup;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.model.context.BotProperties;
import com.yuni.groupbot.model.websocket.AuthMessage;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import com.yuni.groupbot.model.websocket.HeartbeatMessage;
import com.yuni.groupbot.utils.RequestUtil;
import com.yuni.groupbot.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;

/**
 * @author OvO
 * @date 2024/7/9 21:12
 */
@Slf4j

public class EventSubscribeService {
    private WebSocketClient webSocketClient;

    private List<BotEventHandler> messageHandlerList;

    private RequestUtil requestUtil;

    private BotProperties botConfiguration;

    private TokenUtil tokenUtil;

    private Integer index = null;

    public EventSubscribeService(List<BotEventHandler> messageHandlerList, RequestUtil requestUtil, TokenUtil tokenUtil,BotProperties botConfiguration) {
        this.messageHandlerList = messageHandlerList;
        this.requestUtil = requestUtil;
        this.tokenUtil = tokenUtil;
        this.botConfiguration = botConfiguration;
    }

    public void init() {
        try {
            String webSocketUrl = JSONObject.parseObject(requestUtil.get("/gateway")).getString("url");
            webSocketClient = new WebSocketClient(new URI(webSocketUrl)) {
                @Override
                public void onOpen(ServerHandshake open) {
                    AuthMessage message = new AuthMessage(getIntents());
                    tokenUtil.addWebSocketToken(message);
                    webSocketClient.send(JSONObject.toJSONString(message));
                }

                @Override
                public void onMessage(String message) {
                    JSONObject object = JSONObject.parseObject(message);
                    if (object.containsKey("s")) {
                        index = object.getInteger("s");
                    }
                    try {
                        BotWebSocketMessage webSocketMessage = JSONObject.parseObject(message, BotWebSocketMessage.class);
                        log.info("\n[{}]收到WebSocket消息：\n{}", botConfiguration.getName(), JSONUtil.formatJsonStr(message));
                        messageHandlerList.forEach(a -> a.accept(webSocketMessage));
                    } catch (JSONException ignored) {

                    } catch (Exception e) {
                        log.error("消息处理失败：{}", e.getMessage());
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("连接已关闭");
                }

                @Override
                public void onError(Exception ex) {
                    log.error("WebSocket连接发生错误：{}", ex.getMessage(), ex);
                }
            };
            webSocketClient.connect();
            new Thread(() -> {
                while (true) {
                    sendHeartbeatMessage();
                    try {
                        Thread.sleep(botConfiguration.getHeartbeatInterval());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
            log.info("机器人【{}】启动成功!", botConfiguration.getName());
        } catch (Exception e) {

        }
    }

    private void sendHeartbeatMessage() {
        if (webSocketClient.getReadyState() == ReadyState.OPEN) {
            HeartbeatMessage heartbeatMessage = new HeartbeatMessage(index);
            String s = JSONObject.toJSONString(heartbeatMessage);
//            log.info("send heartbeat :{}", s);
            webSocketClient.send(s);
        }
    }

    private Integer getIntents() {
        Set<Integer> intentSet = new HashSet<>();
        Set<BotEvent> eventSet = new HashSet<>();
        for (BotEventHandler handler : messageHandlerList) {
            eventSet.addAll(handler.subscribe());
        }
        for (BotEventGroup eventGroup : BotEventGroup.values()) {
            if (CollUtil.isNotEmpty(CollUtil.intersection(eventSet, eventGroup.getEvents()))) {
                intentSet.add(eventGroup.getIntent());
            }
        }
        int result = 0;
        for (int intent : intentSet) {
            result |= intent;
        }
        return result;
    }

}
