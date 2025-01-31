package com.yuni.groupbot.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuni.groupbot.model.properties.BotProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author OvO
 * @date 2024/7/9 20:25
 */
@Slf4j
@AllArgsConstructor
public class RequestUtil {

    private BotProperties properties;

    public String get(String url) {
        return executeRequest(Method.GET, url, null);

    }

    public String post(String url, Object param) {
        return executeRequest(Method.POST, url, JSONObject.toJSONString(param));
    }


    private String executeRequest(Method method, String url, String body) {
        HttpRequest request = HttpUtil.createRequest(method, properties.getApiHost() + url);
        request.body(body);
        request.header("Authorization", StrUtil.format(
                "Bot {}.{}", properties.getAppId(), properties.getToken()
        ));
        try (HttpResponse execute = request.execute()) {
            String response = execute.body();
            log.info("\n--------------------------------------------------------------------------------------------------------------------------------------" +
                            "\n发起{}请求: {}" +
                            "\nbody:{}" +
                            "\n响应信息: {}" +
                            "\n--------------------------------------------------------------------------------------------------------------------------------------",
                    method.name(), request.getUrl(), body != null ? JSONUtil.formatJsonStr(body) : "null", JSONUtil.formatJsonStr(response));
            return response;
        } catch (Exception e) {
            log.error("请求失败：{}", e.getMessage(), e);
            throw new IllegalStateException("请求失败");
        }
    }

}
