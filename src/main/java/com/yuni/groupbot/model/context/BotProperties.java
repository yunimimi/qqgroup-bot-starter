package com.yuni.groupbot.model.context;

import lombok.Data;

@Data
public class BotProperties {

    private String name;

    private Boolean enable = true;

    private String appId;

    private String secret;

    private String token;

    private String apiHost = "https://sandbox.api.sgroup.qq.com";

    private Long heartbeatInterval = 10 * 1000L;
}