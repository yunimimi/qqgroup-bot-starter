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
import com.yuni.groupbot.model.websocket.ResumeMessage;
import com.yuni.groupbot.utils.MessageSender;
import com.yuni.groupbot.utils.RequestUtil;
import com.yuni.groupbot.utils.TokenUtil;
import lombok.Getter;
import lombok.SneakyThrows;
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

    private String sessionId = "";

    public EventSubscribeService(List<BotEventHandler> messageHandlerList, RequestUtil requestUtil, TokenUtil tokenUtil, MessageSender sender, BotProperties botProperties) {
        this.messageHandlerList = messageHandlerList;
        this.requestUtil = requestUtil;
        this.tokenUtil = tokenUtil;
        this.botProperties = botProperties;
        this.messageSender = sender;
    }

    @SneakyThrows
    public void reconnect(){
        log.info("【{}】定时重连机器人",botProperties.getName());
        webSocketClient.closeBlocking();
        log.info("【{}】webSocketClient关闭成功",botProperties.getName());
        init();
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
                        JSONObject d = webSocketMessage.getD();
                        switch (webSocketMessage.getOp()) {
                            case 10: {
                                Long interval = d.getLong("heartbeat_interval");
                                sendHeartbeatTask(interval);
                                log.info("机器人【{}】启动成功!", botProperties.getName());
                                break;
                            }
                            case 11: {
                                log.debug("heart beat");
                                break;
                            }
                            default: {
                                if (webSocketMessage.getT() == BotEvent.READY) {
                                    log.info("机器人【{}】已连接到服务器", botProperties.getName());
                                    sessionId = d.getString("session_id");
                                    log.info("服务端返回信息：{}", JSONUtil.formatJsonStr(d.toJSONString()));
                                    break;
                                } else if (webSocketMessage.getT() == BotEvent.RESUMED) {
                                    log.info("机器人【{}】重连成功", botProperties.getName());
                                }
                                log.info("\n[{}]收到WebSocket消息：\n{}", botProperties.getName(), JSONUtil.formatJsonStr(message));
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
                        }

                    } catch (JSONException ignored) {

                    } catch (Exception e) {
                        log.error("消息处理失败：{}", e.getMessage(), e);
                    }
                }

                @SneakyThrows
                @Override
                public void onClose(int code, String reason, boolean remote) {
                    log.error("【{}】连接关闭【{}】:{} remote:{}", code, botProperties.getName(), reason, remote);
                    if (code == 4009) {
                        log.info("尝试重连");

                    }
                }

                @Override
                public void onError(Exception ex) {
                    log.error("WebSocket连接发生错误：{}", ex.getMessage(), ex);
                }
            };
            webSocketClient.connectBlocking();
        } catch (Exception e) {

        }
    }

    public void sendHeartbeatMessage() {
        if (isConnectOpen()) {
            HeartbeatMessage heartbeatMessage = new HeartbeatMessage(index);
            String s = JSONObject.toJSONString(heartbeatMessage);
//            log.info("send heartbeat :{}", s);
            webSocketClient.send(s);
        }
    }


    /**
     * 在连接断开时尝试重连
     *
     * @return
     */
    public boolean isConnectOpen() {
        // 连接断开
        while (!webSocketClient.isOpen()) {
            try {
                Thread.sleep(200);
                if (webSocketClient.getReadyState().equals(ReadyState.CLOSING) || webSocketClient.getReadyState().equals(ReadyState.CLOSED)) {
                    webSocketClient.reconnectBlocking();
                    ResumeMessage resumeMessage = new ResumeMessage(tokenUtil.getWebSocketToken(), sessionId, index);
                    webSocketClient.send(JSONObject.toJSONString(resumeMessage));
                    log.info("重连成功...");
                    return true;
                }
            } catch (Exception e) {
                log.error("reconnect error ", e);
                return false;
            }

        }
        return true;
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
