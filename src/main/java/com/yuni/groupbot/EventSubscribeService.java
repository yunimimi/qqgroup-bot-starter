package com.yuni.groupbot;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.config.BotConfiguration;
import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.enums.BotEventGroup;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.model.websocket.AuthMessage;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import com.yuni.groupbot.model.websocket.HeartbeatMessage;
import com.yuni.groupbot.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author OvO
 * @date 2024/7/9 21:12
 */
@Slf4j
public class EventSubscribeService implements InitializingBean {
    private WebSocketClient webSocketClient;

    private List<BotEventHandler> messageHandlerList;

    private RequestUtil requestUtil;

    private BotConfiguration botConfiguration;

    private Integer index = null;


    @Override
    public void afterPropertiesSet() throws Exception {
        String webSocketUrl = JSONObject.parseObject(requestUtil.get("/gateway")).getString("url");
        webSocketClient = new WebSocketClient(new URI(webSocketUrl)) {
            @Override
            public void onOpen(ServerHandshake open) {
                AuthMessage message = new AuthMessage(getIntents());
                webSocketClient.send(JSONObject.toJSONString(message));
            }

            @Override
            public void onMessage(String message) {
                log.info("\n收到WebSocket消息：\n{}", JSONUtil.formatJsonStr(message));
                JSONObject object = JSONObject.parseObject(message);
                if (object.containsKey("s")) {
                    index = object.getInteger("s");
                }
                try {
                    BotWebSocketMessage webSocketMessage = JSONObject.parseObject(message, BotWebSocketMessage.class);
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
                ex.printStackTrace();
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

    }

    private void sendHeartbeatMessage() {
        if (webSocketClient.getReadyState() == ReadyState.OPEN) {
            HeartbeatMessage heartbeatMessage = new HeartbeatMessage(index);
            String s = JSONObject.toJSONString(heartbeatMessage);
            log.info("send heartbeat :{}", s);
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
