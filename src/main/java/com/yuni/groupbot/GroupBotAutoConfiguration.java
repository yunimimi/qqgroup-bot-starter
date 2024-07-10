package com.yuni.groupbot;

import com.yuni.groupbot.config.BotConfiguration;
import com.yuni.groupbot.handler.BotEventHandler;
import com.yuni.groupbot.handler.impl.DemoBotEventHandler;
import com.yuni.groupbot.service.EventSubscribeService;
import com.yuni.groupbot.utils.MessageSender;
import com.yuni.groupbot.utils.RequestUtil;
import com.yuni.groupbot.utils.TokenUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
    }


    @Bean
    public RequestUtil requestUtil() {
        return new RequestUtil(botConfiguration);
    }

    @Bean
    public TokenUtil tokenUtil() {
        return new TokenUtil(botConfiguration);
    }

    @Bean
    public MessageSender messageSender() {
        return new MessageSender();
    }

    @Bean
    public EventSubscribeService eventSubscribeService(){
        return new EventSubscribeService();
    }
    @Bean
    @ConditionalOnMissingBean(BotEventHandler.class)
    public BotEventHandler demoBotEventHandler(){
        return new DemoBotEventHandler();
    }

}
