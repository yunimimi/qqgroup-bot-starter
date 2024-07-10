package com.yuni.groupbot.model.websocket;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.utils.TokenUtil;
import lombok.Data;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 上午11:43
 */
@Data
public class AuthMessage {

    private Integer op = 2;

    private JSONObject d;

    public AuthMessage(Integer intents) {
        JSONObject d = new JSONObject();
        d.put("intents", intents);
        this.d = d;
        TokenUtil tokenUtil = SpringUtil.getBean(TokenUtil.class);
        tokenUtil.addWebSocketToken(this);
    }
}
