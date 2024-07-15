package com.yuni.groupbot.config;

import com.yuni.groupbot.model.properties.BotProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * @author OvO
 * @date 2024/7/9 20:20
 */
@Data
@ConfigurationProperties(prefix = "bot")
public class BotConfiguration {

    private List<BotProperties> propertiesList;

    private Map<String,String> keywordReplaceMap;

}
