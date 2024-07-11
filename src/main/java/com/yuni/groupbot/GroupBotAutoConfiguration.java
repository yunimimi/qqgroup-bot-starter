package com.yuni.groupbot;

import cn.hutool.extra.spring.SpringUtil;
import com.yuni.groupbot.config.BotConfiguration;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.handler.impl.DemoBotEventHandler;
import com.yuni.groupbot.model.context.BotProperties;
import com.yuni.groupbot.service.BotService;
import com.yuni.groupbot.service.EventSubscribeService;
import com.yuni.groupbot.utils.MessageSender;
import com.yuni.groupbot.utils.RequestUtil;
import com.yuni.groupbot.utils.TokenUtil;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
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
        Map<String, BotEventHandler> handlerMap = SpringUtil.getBeansOfType(BotEventHandler.class);
        for (BotProperties botProperties : botConfiguration.getPropertiesList()) {
            if (!botProperties.getEnable()) {
                continue;
            }
            List<BotEventHandler> all = handlerMap.values().stream()
                    .filter(a -> a.botName().contains(botProperties.getName())).collect(Collectors.toList());
            SpringUtil.registerBean(botProperties.getName(), new BotService(botProperties, all));
        }
    }
}
