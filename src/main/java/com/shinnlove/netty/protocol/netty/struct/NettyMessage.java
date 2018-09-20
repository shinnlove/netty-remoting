/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.struct;

/**
 * 私有netty协议消息报文。
 *
 * @author shinnlove.jinsheng
 * @version $Id: NettyMessage.java, v 0.1 2018-06-29 下午1:13 shinnlove.jinsheng Exp $$
 */
public final class NettyMessage {

    /** 报文头 */
    private Header header;

    /** 报文体 */
    private Object body;

    /**
     * @return the header
     */
    public final Header getHeader() {
        return header;
    }

    /**
     * @param header
     *            the header to set
     */
    public final void setHeader(Header header) {
        this.header = header;
    }

    /**
     * @return the body
     */
    public final Object getBody() {
        return body;
    }

    /**
     * @param body
     *            the body to set
     */
    public final void setBody(Object body) {
        this.body = body;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NettyMessage [header=" + header + "]";
    }
}