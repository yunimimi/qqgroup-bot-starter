package com.yuni.groupbot.model.properties;

import lombok.Data;

import java.util.List;

@Data
public class BotProperties {

    private String name;

    private Boolean enable = true;

    private String appId;

    private String secret;

    private String token;

    private String apiHost = "https://sandbox.api.sgroup.qq.com";

    private Long heartbeatInterval = 10 * 1000L;

    private List<String> eventHandlers;
}