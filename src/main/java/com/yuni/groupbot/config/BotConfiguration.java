package com.yuni.groupbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author OvO
 * @date 2024/7/9 20:20
 */
@Data
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {

    private String name;

    private String appId;

    private String secret;

    private String token;

    private String apiHost;

    private Long heartbeatInterval;
}
