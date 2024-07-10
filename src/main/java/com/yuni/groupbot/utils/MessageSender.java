package com.yuni.groupbot.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.model.http.SendMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 下午2:50
 */
public class MessageSender {

    private  final int TARGET_TYPE_USER = 0;

    private  final int TARGET_TYPE_GROUP = 1;

    private  RequestUtil requestUtil;

    public MessageSender(RequestUtil requestUtil) {
        this.requestUtil = requestUtil;
    }

    public  void sendMessage2Group(String groupId, String message) {
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(message);
        dto.setMsg_type(0);
        sendMessage(groupId, TARGET_TYPE_GROUP, dto);
    }
    public  void sendMessage2User(String userId, String message) {
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(message);
        dto.setMsg_type(0);
        sendMessage(userId, TARGET_TYPE_USER, dto);
    }
    public  void reply2Group(String groupId,String msgId,String message){
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(message);
        dto.setMsg_type(0);
        dto.setMsg_id(msgId);
        sendMessage(groupId, TARGET_TYPE_GROUP, dto);
    }

    public  void reply2User(String userId,String msgId,String message){
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(message);
        dto.setMsg_type(0);
        dto.setMsg_id(msgId);
        sendMessage(userId, TARGET_TYPE_USER, dto);
    }

    private  void sendMessage(String id, int targetType, SendMessageDTO sendMessageDTO) {
        String url = targetType == 0 ? StrUtil.format("/v2/users/{}/messages", id) : StrUtil.format("/v2/groups/{}/messages", id);
        requestUtil.post(url, sendMessageDTO);
    }


}
