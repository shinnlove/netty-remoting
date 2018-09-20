/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.shinnlove.netty.protocol.netty.struct.Header;
import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Netty消息解码器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: NettyMessageDecoder.java, v 0.1 2018-06-29 下午1:09 shinnlove.jinsheng Exp $$
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private MarshallingDecoder marshallingDecoder;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength)
                                                                                                throws IOException {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        marshallingDecoder = new MarshallingDecoder();
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 使用`LengthFieldBasedFrameDecoder`解码，如果读到null，代表半包，直接返回null
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        System.out.println("开始解码消息");

        // 从字节缓存中读取消息头
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(frame.readInt());
        header.setLength(frame.readInt());
        header.setSessionID(frame.readLong());
        header.setType(frame.readByte());
        header.setPriority(frame.readByte());

        // 读取头部附件

        // 读取可变长头部附件长度
        int size = frame.readInt();
        if (size > 0) {
            // 如果有附加数据
            Map<String, Object> attch = new HashMap<String, Object>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            // 循环读取key和value
            for (int i = 0; i < size; i++) {
                // key长度(key的字节数)
                keySize = frame.readInt();
                // 字节数组
                keyArray = new byte[keySize];
                // 读出这么多字节放入`keyArray`中
                frame.readBytes(keyArray);
                // key是utf-8编码的，如：Connection:Keep-Alive;
                key = new String(keyArray, "UTF-8");
                // 然后是key对应的值，值首先也有一个长度，然后是内容
                attch.put(key, marshallingDecoder.decode(frame));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attch);
        }

        // 消息头附件后边如果还有4字节，说明消息体不为空，且是消息体的长度
        if (frame.readableBytes() > 4) {
            // 解码消息体
            message.setBody(marshallingDecoder.decode(frame));
        }

        // 设置消息头
        message.setHeader(header);

        return message;
    }

}