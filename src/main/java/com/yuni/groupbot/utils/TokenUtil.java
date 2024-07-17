package com.yuni.groupbot.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.model.properties.BotProperties;
import com.yuni.groupbot.model.websocket.AuthMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 上午11:41
 */
@Slf4j
public class TokenUtil {

    private BotProperties properties;

    public TokenUtil(BotProperties properties) {
        this.properties = properties;
        refreshAccessToken();
    }

    public boolean expiringSoon() {
        return System.currentTimeMillis() > expireAt - (30 * 1000);
    }

    private String accessToken = "";

    private Long expireAt = 0L;


    public void addWebSocketToken(AuthMessage message) {
        JSONObject d = message.getD();
        d.put("token", getWebSocketToken());
    }

    public void refreshAccessToken() {
        HttpRequest post = HttpUtil.createPost("https://bots.qq.com/app/getAppAccessToken");
        HashMap<String, String> map = new HashMap<>();
        map.put("appId", properties.getAppId());
        map.put("clientSecret", properties.getSecret());
        post.body(JSONObject.toJSONString(map));
        try (HttpResponse execute = post.execute()) {
            String body = execute.body();
            JSONObject resp = JSONObject.parseObject(body);
            accessToken = resp.getString("access_token");
            expireAt = System.currentTimeMillis() + (resp.getInteger("expires_in") * 1000);
        }
    }

    public  String getWebSocketToken() {
        return StrUtil.format("QQBot {}", accessToken);
    }
}
