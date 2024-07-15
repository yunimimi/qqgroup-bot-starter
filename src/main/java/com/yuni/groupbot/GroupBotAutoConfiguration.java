package com.yuni.groupbot;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.yuni.groupbot.config.BotConfiguration;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.handler.impl.HelpCommandHandler;
import com.yuni.groupbot.model.properties.BotProperties;
import com.yuni.groupbot.service.BotService;
import com.yuni.groupbot.task.TimedTask;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 上午9:25
 */
@Configuration
@EnableConfigurationProperties(BotConfiguration.class)
public class GroupBotAutoConfiguration {

    private final BotConfiguration botConfiguration;

    public GroupBotAutoConfiguration(BotConfiguration botConfiguration) {
        this.botConfiguration = botConfiguration;
        SpringUtil.registerBean("helpCommandHandler", new HelpCommandHandler());

        for (BotProperties botProperties : botConfiguration.getPropertiesList()) {
            if (!botProperties.getEnable() || CollectionUtil.isEmpty(botProperties.getEventHandlers())) {
                continue;
            }
            List<BotEventHandler> handlers = new ArrayList<>();
            handlers.add(SpringUtil.getBean(HelpCommandHandler.class));
            for (String string : botProperties.getEventHandlers()) {
                BotEventHandler handler = SpringUtil.getBean(string, BotEventHandler.class);
                handlers.add(handler);
            }
            SpringUtil.registerBean(botProperties.getName(), new BotService(botProperties, handlers, botConfiguration.getKeywordReplaceMap()));
        }
    }

    @Bean
    public TimedTask timedTask() {
        return new TimedTask();
    }
}
