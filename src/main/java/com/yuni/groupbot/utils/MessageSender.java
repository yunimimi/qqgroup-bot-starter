package com.yuni.groupbot.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.model.http.SendMessageDTO;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 下午2:50
 */
public class MessageSender {

    private final int TARGET_TYPE_USER = 0;

    private final int TARGET_TYPE_GROUP = 1;

    @Autowired
    private RequestUtil requestUtil;

    public void reply2Group(BotWebSocketMessage botWebSocketMessage, String content) {
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(content);
        dto.setMsg_type(0);
        dto.setMsg_id(botWebSocketMessage.getMsgId());
        sendMessage(botWebSocketMessage.getGroupId(), TARGET_TYPE_GROUP, dto);
    }


//    public void reply2User(BotWebSocketMessage botWebSocketMessage, String content) {
//        SendMessageDTO dto = new SendMessageDTO();
//        dto.setContent(content);
//        dto.setMsg_type(0);
//        dto.setMsg_id(botWebSocketMessage.getMsgId());
//        sendMessage(botWebSocketMessage.getGroupId(), TARGET_TYPE_GROUP, dto);
//    }

    private void sendMessage(String id, int targetType, SendMessageDTO sendMessageDTO) {
        String url = targetType == 0 ? StrUtil.format("/v2/users/{}/messages", id) : StrUtil.format("/v2/groups/{}/messages", id);
        requestUtil.post(url, sendMessageDTO);
    }


}
