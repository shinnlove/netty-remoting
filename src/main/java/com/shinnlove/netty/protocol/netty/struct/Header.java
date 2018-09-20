/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.struct;

import java.util.HashMap;
import java.util.Map;

/**
 * 私有netty协议栈消息请求头。
 *
 * @author shinnlove.jinsheng
 * @version $Id: Header.java, v 0.1 2018-06-29 下午1:12 shinnlove.jinsheng Exp $$
 */
public final class Header {

    /** CRC校验码 */
    private int                 crcCode    = 0xabef0101;

    /** 消息长度 */
    private int                 length;

    /** 登录后的分布式会话ID */
    private long                sessionID;

    /** 消息类型 */
    private byte                type;

    /** 消息优先级 */
    private byte                priority;

    /** 消息头附件 */
    private Map<String, Object> attachment = new HashMap<String, Object>();

    /**
     * @return the crcCode
     */
    public final int getCrcCode() {
        return crcCode;
    }

    /**
     * @param crcCode
     *            the crcCode to set
     */
    public final void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    /**
     * @return the length
     */
    public final int getLength() {
        return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public final void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the sessionID
     */
    public final long getSessionID() {
        return sessionID;
    }

    /**
     * @param sessionID
     *            the sessionID to set
     */
    public final void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * @return the type
     */
    public final byte getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public final void setType(byte type) {
        this.type = type;
    }

    /**
     * @return the priority
     */
    public final byte getPriority() {
        return priority;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public final void setPriority(byte priority) {
        this.priority = priority;
    }

    /**
     * @return the attachment
     */
    public final Map<String, Object> getAttachment() {
        return attachment;
    }

    /**
     * @param attachment
     *            the attachment to set
     */
    public final void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Header [crcCode=" + crcCode + ", length=" + length + ", sessionID=" + sessionID
               + ", type=" + type + ", priority=" + priority + ", attachment=" + attachment + "]";
    }

}