package com.yuni.groupbot.model;

import com.yuni.groupbot.enums.BotEvent;
import com.yuni.groupbot.model.properties.BotProperties;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author zhuangwenqiang
 * @date 2024/7/15 上午9:14
 */
@Data
@ToString
public class BotEventContext {

    private BotProperties botProperties;

    private String userId;

    private String groupId;

    private String msgId;

    private String content;

    private String reply;

    private BotEvent event;
}
