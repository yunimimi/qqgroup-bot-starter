package com.yuni.groupbot.model.websocket;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author zhuangwenqiang
 * @date 2024/7/17 下午2:15
 */
@Data
public class ResumeMessage {

    private Integer op = 6;

    private JSONObject d;

    public ResumeMessage(String token,String sessionId,Integer index) {
        JSONObject d = new JSONObject();
        d.put("token", token);
        d.put("session_id", sessionId);
        d.put("seq", index);
        this.d = d;

    }
}
