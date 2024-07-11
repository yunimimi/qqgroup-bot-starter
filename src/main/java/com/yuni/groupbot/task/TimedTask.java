package com.yuni.groupbot.task;

import cn.hutool.extra.spring.SpringUtil;
import com.yuni.groupbot.service.BotService;
import com.yuni.groupbot.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author OvO
 * @date 2024/7/11 19:29
 */
@Slf4j
@Component
public class TimedTask {

    private Collection<BotService> services;

    @Scheduled(initialDelay = 20000, fixedDelay = 20000)
    public void refreshToken() {
        if (services == null) {
            init();
        }
        for (BotService service : services) {
            TokenUtil tokenUtil = service.getTokenUtil();
            if (tokenUtil.expiringSoon()) {
                log.info("token即将过期，刷新token");
                tokenUtil.refreshAccessToken();
            }
        }
    }



    private void init() {
        services = SpringUtil.getBeansOfType(BotService.class).values();
    }


}
