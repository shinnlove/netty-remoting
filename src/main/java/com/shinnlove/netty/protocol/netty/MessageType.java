/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty;

/**
 * Netty私有协议栈报文类型。
 *
 * @author shinnlove.jinsheng
 * @version $Id: MessageType.java, v 0.1 2018-06-29 下午1:13 shinnlove.jinsheng Exp $$
 */
public enum MessageType {

    /** 服务请求 */
    SERVICE_REQ((byte) 0),

    /** 服务响应 */
    SERVICE_RESP((byte) 1),

    /** 单步 */
    ONE_WAY((byte) 2),

    /** 登录请求 */
    LOGIN_REQ((byte) 3),

    /** 登录响应 */
    LOGIN_RESP((byte) 4),

    /** 心跳请求 */
    HEARTBEAT_REQ((byte) 5),

    /** 心跳响应 */
    HEARTBEAT_RESP((byte) 6);

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

}