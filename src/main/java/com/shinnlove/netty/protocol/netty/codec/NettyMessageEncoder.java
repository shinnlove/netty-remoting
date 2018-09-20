/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.codec;

import java.io.IOException;
import java.util.Map;

import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Netty消息加密器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: NettyMessageEncoder.java, v 0.1 2018-06-29 下午1:10 shinnlove.jinsheng Exp $$
 */
public final class NettyMessageEncoder extends MessageToByteEncoder<NettyMessage> {

    private MarshallingEncoder marshallingEncoder;

    public NettyMessageEncoder() throws IOException {
        this.marshallingEncoder = new MarshallingEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, ByteBuf sendBuf)
                                                                                       throws Exception {
        if (msg == null || msg.getHeader() == null)
            throw new Exception("The encode message is null");

        // 消息头：CRC校验头、消息头长度、sessionId、消息类型、消息优先级、消息附件长度
        // 32位int整型->4KB
        sendBuf.writeInt((msg.getHeader().getCrcCode()));
        sendBuf.writeInt((msg.getHeader().getLength()));
        // 64位long整型->8KB
        sendBuf.writeLong((msg.getHeader().getSessionID()));
        // 8位字节->1KB
        sendBuf.writeByte((msg.getHeader().getType()));
        sendBuf.writeByte((msg.getHeader().getPriority()));
        // 32位int整型->4KB
        sendBuf.writeInt((msg.getHeader().getAttachment().size()));

        // 先编码消息头附件
        String key = null;
        byte[] keyArray = null;
        Object value = null;
        for (Map.Entry<String, Object> param : msg.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(value, sendBuf);
        }
        key = null;
        keyArray = null;
        value = null;

        // 再编码消息体
        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        } else {
            sendBuf.writeInt(0);
        }

        // 最后，等全部编码结束后才知道字节数组大小、更新消息体长度
        sendBuf.setInt(4, sendBuf.readableBytes() - 8);
    }

}