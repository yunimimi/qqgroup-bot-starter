package com.yuni.groupbot.config;

import com.yuni.groupbot.model.context.BotProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author OvO
 * @date 2024/7/9 20:20
 */
@Data
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {

    private List<BotProperties> propertiesList;


}
