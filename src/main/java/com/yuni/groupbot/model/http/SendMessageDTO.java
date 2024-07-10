package com.yuni.groupbot.model.http;

import lombok.Data;

/**
 * 机器人推送消息请求
 * <p>
 * 常见错误码
 * 当 msg_type = 7 时，content 字段需要填入一个值，譬如一个空格 “ ”，后续版本会修复该问题。、
 * code	message	说明
 * 22009	msg limit exceed	消息发送超频
 * 304082	upload media info fail	富媒体资源拉取失败，请重试
 * 304083	convert media info fail	富媒体资源拉取失败，请重试
 *
 * @author zhuangwenqiang
 * @date 2024/7/10 下午2:55
 */
@Data
public class SendMessageDTO {
    private String content;
    /**
     * 消息类型：0 是文本，2 是 markdown， 3 ark，4 embed，7 media 富媒体
     */
    private Integer msg_type;
    private Object markdown;
    private Object keyboard;
    private Object ark;
    private Object media;
    /**
     * 	【暂未支持】消息引用
     */
    private Object message_reference;
    /**
     * 	前置收到的事件 ID，用于发送被动消息，支持事件："INTERACTION_CREATE"、"C2C_MSG_RECEIVE"、"FRIEND_ADD"
     */
    private String event_id;
    /**
     * 前置收到的用户发送过来的消息 ID，用于发送被动（回复）消息
     */
    private String msg_id;
    /**
     * 回复消息的序号，与 msg_id 联合使用，避免相同消息id回复重复发送，不填默认是1。相同的 msg_id + msg_seq 重复发送会失败。
     */
    private String msg_seq;


}
