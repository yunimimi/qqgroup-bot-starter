package com.yuni.groupbot.model.websocket;

import com.yuni.groupbot.enums.BotEvent;
import lombok.Data;

/**
 * @author zhuangwenqiang
 * @date 2024/7/10 上午9:57
 */
@Data
public class BotWebSocketMessage {

    /**
     * op 指的是 opcode
     * 0	Dispatch	Receive	服务端进行消息推送
     * 1	Heartbeat	Send/Receive	客户端或服务端发送心跳
     * 2	Identify	Send	客户端发送鉴权
     * 6	Resume	Send	客户端恢复连接
     * 7	Reconnect	Receive	服务端通知客户端重新连接
     * 9	Invalid Session	Receive	当 identify 或 resume 的时候，如果参数有错，服务端会返回该消息
     * 10	Hello	Receive	当客户端与网关建立 ws 连接之后，网关下发的第一条消息
     * 11	Heartbeat ACK	Receive/Reply	当发送心跳成功之后，就会收到该消息
     * 12	HTTP Callback ACK	Reply	仅用于 http 回调模式的回包，代表机器人收到了平台推送的数据
     * 客户端行为含义如下：
     * Receive 客户端接收到服务端 push 的消息
     * Send 客户端发送消息
     * Reply 客户端接收到服务端发送的消息之后的回包（HTTP 回调模式）
     */
    private Integer op;
    /**
     * s 下行消息都会有一个序列号，标识消息的唯一性，客户端需要再发送心跳的时候，携带客户端收到的最新的s。
     */
    private Integer s;
    /**
     * t 代表事件类型。
     */
    private BotEvent t;
    /**
     * d 代表事件内容，不同事件类型的事件内容格式都不同，请注意识别。
     */
    private Object d;
}
