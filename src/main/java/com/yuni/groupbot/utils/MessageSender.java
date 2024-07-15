package com.yuni.groupbot.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.model.BotEventContext;
import com.yuni.groupbot.model.http.SendMessageDTO;
import com.yuni.groupbot.model.websocket.BotWebSocketMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.dc.pr.PRError;

import java.util.Map;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 下午2:50
 */
@AllArgsConstructor
public class MessageSender {

    private final int TARGET_TYPE_USER = 0;

    private final int TARGET_TYPE_GROUP = 1;

    private RequestUtil requestUtil;

    private Map<String,String> replaceMap;

    public void reply(BotEventContext context) {
        if (context.getGroupId() != null) {
            reply2Group(context);
        } else {
            reply2User(context);
        }
    }

    public void reply2Group(BotEventContext context) {
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(context.getReply());
        dto.setMsg_type(0);
        dto.setMsg_id(context.getMsgId());
        sendMessage(context.getGroupId(), TARGET_TYPE_GROUP, dto);
    }


    public void reply2User(BotEventContext context) {
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(context.getReply());
        dto.setMsg_type(0);
        dto.setMsg_id(context.getMsgId());
        sendMessage(context.getUserId(), TARGET_TYPE_USER, dto);
    }

    private void sendMessage(String id, int targetType, SendMessageDTO sendMessageDTO) {
        String url = targetType == TARGET_TYPE_USER ? StrUtil.format("/v2/users/{}/messages", id) : StrUtil.format("/v2/groups/{}/messages", id);
        requestUtil.post(url, sendMessageDTO);
    }


}
