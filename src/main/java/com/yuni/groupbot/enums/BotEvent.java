package com.yuni.groupbot.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GUILD 频道
 * GROUP 群
 * C2C 私聊
 *
 * @author zhuangwenqiang
 * @date 2024/7/9 22:27
 */
@AllArgsConstructor
@Getter
public enum BotEvent {


    /**
     * 当机器人加入新guild时
     */
    GUILD_CREATE,
    /**
     * 当guild资料发生变更时
     */
    GUILD_UPDATE,
    /**
     * 当机器人退出guild时
     */
    GUILD_DELETE,
    /**
     * 当channel被创建时
     */
    CHANNEL_CREATE,
    /**
     * 当channel被更新时
     */
    CHANNEL_UPDATE,
    /**
     * 当channel被删除时
     */
    CHANNEL_DELETE,

    /**
     * 当成员加入时
     */
    GUILD_MEMBER_ADD,
    /**
     * 当成员资料变更时
     */
    GUILD_MEMBER_UPDATE,
    /**
     * 当成员被移除时
     */
    GUILD_MEMBER_REMOVE,

    /**
     * 发送消息事件，代表频道内的全部消息，而不只是 at 机器人的消息。内容与 AT_MESSAGE_CREATE 相同
     */
    MESSAGE_CREATE,
    /**
     * 删除（撤回）消息事件
     */
    MESSAGE_DELETE,


    /**
     * 为消息添加表情表态
     */
    MESSAGE_REACTION_ADD,
    /**
     * 为消息删除表情表态
     */
    MESSAGE_REACTION_REMOVE,


    /**
     * 当收到用户发给机器人的私信消息时
     */
    DIRECT_MESSAGE_CREATE,
    /**
     * 删除（撤回）消息事件
     */
    DIRECT_MESSAGE_DELETE,


    /**
     * 用户单聊发消息给机器人时候
     */
    C2C_MESSAGE_CREATE,
    /**
     * 用户添加使用机器人
     */
    FRIEND_ADD,
    /**
     * 用户删除机器人
     */
    FRIEND_DEL,
    /**
     * 用户在机器人资料卡手动关闭"主动消息"推送
     */
    C2C_MSG_REJECT,
    /**
     * 用户在机器人资料卡手动开启"主动消息"推送开关
     */
    C2C_MSG_RECEIVE,
    /**
     * 用户在群里@机器人时收到的消息
     */
    GROUP_AT_MESSAGE_CREATE,
    /**
     * 机器人被添加到群聊
     */
    GROUP_ADD_ROBOT,
    /**
     * 机器人被移出群聊
     */
    GROUP_DEL_ROBOT,
    /**
     * 群管理员主动在机器人资料页操作关闭通知
     */
    GROUP_MSG_REJECT,
    /**
     * 群管理员主动在机器人资料页操作开启通知
     */
    GROUP_MSG_RECEIVE,

    /**
     * 互动事件创建时
     */
    INTERACTION_CREATE,

    /**
     * 消息审核通过
     */
    MESSAGE_AUDIT_PASS,
    /**
     * 消息审核不通过
     */
    MESSAGE_AUDIT_REJECT,

    /**
     * 当用户创建主题时
     */
    FORUM_THREAD_CREATE,
    /**
     * 当用户更新主题时
     */
    FORUM_THREAD_UPDATE,
    /**
     * 当用户删除主题时
     */
    FORUM_THREAD_DELETE,
    /**
     * 当用户创建帖子时
     */
    FORUM_POST_CREATE,
    /**
     * 当用户删除帖子时
     */
    FORUM_POST_DELETE,
    /**
     * 当用户回复评论时（注意：FORUM_REPLY_DELETE的注释可能也是类似的，但看起来像是重复了，这里假设它是删除回复）
     */
    FORUM_REPLY_CREATE,
    /**
     * 当用户删除回复时
     */
    FORUM_REPLY_DELETE,
    /**
     * 当用户发表的内容审核通过时
     */
    FORUM_PUBLISH_AUDIT_RESULT,

    /**
     * 音频开始播放时
     */
    AUDIO_START,
    /**
     * 音频播放结束时
     */
    AUDIO_FINISH,
    /**
     * 上麦时（即用户开始发言或参与音频聊天）
     */
    AUDIO_ON_MIC,
    /**
     * 下麦时（即用户停止发言或退出音频聊天）
     */
    AUDIO_OFF_MIC,

    /**
     * 当收到@机器人的消息时（即某人在群组中@机器人并发送消息）
     */
    AT_MESSAGE_CREATE,
    /**
     * 当频道的消息被删除时（注意：这里的“频道”可能指的是群组中的某个聊天频道或整个群组）
     */
    PUBLIC_MESSAGE_DELETE,

    READY,
    RESUMED,

    ;

}
