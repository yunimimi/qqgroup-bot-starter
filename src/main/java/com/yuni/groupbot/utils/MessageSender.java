package com.yuni.groupbot.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.model.BotEventContext;
import com.yuni.groupbot.model.http.MediaDTO;
import com.yuni.groupbot.model.http.SendMessageDTO;
import lombok.AllArgsConstructor;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 下午2:50
 */
@AllArgsConstructor
public class MessageSender {

    private RequestUtil requestUtil;

    public void reply(BotEventContext context) {
        switch (context.getMsgType()) {
            case 0:
                replyMessage(context);
                break;
            case 7:
                replyMedia(context);
                break;
        }
    }

    private void replyMedia(BotEventContext context) {
        String fileUrl = context.getReply();
        if (fileUrl == null) {
            return;
        }
        String postUrl;
        if (context.getGroupId() == null) {
            postUrl = StrUtil.format("/v2/users/{}/files", context.getUserId());
        } else {
            postUrl = StrUtil.format("/v2/groups/{}/files", context.getGroupId());
        }
        JSONObject param = new JSONObject();
        param.put("file_type", context.getMediaType());
        param.put("url", fileUrl);
        param.put("srv_send_msg", false);

        String response = requestUtil.post(postUrl, param);
        JSONObject object = JSONObject.parseObject(response);
        String fileInfo = object.getString("file_info");

        SendMessageDTO dto = new SendMessageDTO();
        dto.setMsg_type(7);
        dto.setMsg_id(context.getMsgId());
        MediaDTO mediaDTO = new MediaDTO();
        mediaDTO.setFile_info(fileInfo);
        dto.setMedia(mediaDTO);
        sendMessage(context, dto);
    }


    private void replyMessage(BotEventContext context) {
        SendMessageDTO dto = new SendMessageDTO();
        dto.setContent(context.getReply());
        dto.setMsg_type(0);
        dto.setMsg_id(context.getMsgId());
        sendMessage(context, dto);
    }

    private void sendMessage(BotEventContext context, SendMessageDTO sendMessageDTO) {
        String url;
        if (context.getGroupId() == null) {
            url = StrUtil.format("/v2/users/{}/messages", context.getUserId());
        } else {
            sendMessageDTO.setGroup_openid(context.getGroupId());
            url = StrUtil.format("/v2/groups/{}/messages", context.getGroupId());
        }
        requestUtil.post(url, sendMessageDTO);
    }

}
