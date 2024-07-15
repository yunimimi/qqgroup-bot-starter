package com.yuni.groupbot.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.enums.BotEventGroup;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.model.BotEventContext;
import com.yuni.groupbot.model.properties.BotProperties;
import com.yuni.groupbot.model.websocket.AuthMessage;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import com.yuni.groupbot.model.websocket.HeartbeatMessage;
import com.yuni.groupbot.utils.MessageSender;
import com.yuni.groupbot.utils.RequestUtil;
import com.yuni.groupbot.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

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

    private BotProperties botProperties;

    private TokenUtil tokenUtil;

    private MessageSender messageSender;

    private Integer index = null;

    public EventSubscribeService(List<BotEventHandler> messageHandlerList, RequestUtil requestUtil, TokenUtil tokenUtil, MessageSender sender, BotProperties botProperties) {
        this.messageHandlerList = messageHandlerList;
        this.requestUtil = requestUtil;
        this.tokenUtil = tokenUtil;
        this.botProperties = botProperties;
        this.messageSender = sender;
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
                        log.info("\n[{}]收到WebSocket消息：\n{}", botProperties.getName(), JSONUtil.formatJsonStr(message));
                        if (webSocketMessage.getOp() == 10) {
                            Long interval = webSocketMessage.getD().getLong("heartbeat_interval");
                            sendHeartbeatTask(interval);
                        } else {
                            BotEventContext context = new BotEventContext();
                            context.setEvent(webSocketMessage.getT());
                            context.setBotProperties(botProperties);
                            context.setContent(webSocketMessage.getContent());
                            context.setUserId(webSocketMessage.getUserId());
                            context.setGroupId(webSocketMessage.getGroupId());
                            context.setMsgId(webSocketMessage.getMsgId());
                            log.info("context：{}", context);
                            for (BotEventHandler handler : messageHandlerList) {
                                if (handler.subscribe().contains(context.getEvent()) && handler.match(context)) {
                                    log.info("{} match", handler.getClass().getSimpleName());
                                    handler.handle(context);
                                    handler.postProcessing(context);
                                    if (context.getReply() != null) {
                                        log.info("reply {} ", context.getReply());
                                        messageSender.reply(context);
                                    }
                                    break;
                                }
                            }
                        }
                    } catch (JSONException ignored) {

                    } catch (Exception e) {
                        log.error("消息处理失败：{}", e.getMessage(), e);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.error("【{}】连接关闭:{}", botProperties.getName(), reason);
                }

                @Override
                public void onError(Exception ex) {
                    log.error("WebSocket连接发生错误：{}", ex.getMessage(), ex);
                }
            };
            webSocketClient.connect();
            log.info("机器人【{}】启动成功!", botProperties.getName());
        } catch (Exception e) {

        }
    }

    public void sendHeartbeatMessage() {
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

    private void sendHeartbeatTask(Long sleep) {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                sendHeartbeatMessage();
            }
        }).start();
    }

}
