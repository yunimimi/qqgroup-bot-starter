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

    /**
     * 0 是文本，2 是 markdown， 3 ark，4 embed，7 media 富媒体
     */
    private Integer msgType = 0;

    /**
     *  1 图片，2 视频，3 语音
     */
    private Integer mediaType;

    private String reply;

    private BotEvent event;
}
