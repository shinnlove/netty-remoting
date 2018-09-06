/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.codec;

import java.io.IOException;

import org.jboss.marshalling.Marshaller;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * @author shinnlove.jinsheng
 * @version $Id: MarshallingEncoder.java, v 0.1 2018-06-29 下午1:09 shinnlove.jinsheng Exp $$
 */
@Sharable
public class MarshallingEncoder {

    /** int类型，4字节占位常量 */
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    Marshaller                  marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    /**
     * 将信息序列化编码的函数。
     *
     * 思路：
     * Step1：先写入一个int类型32位4字节的数值，代表消息内容长度。
     * Step2：再直接将消息内容写入字节缓冲中，当`Marshalling`写入`ByteBuf`时，写指针也会变化。
     * Step3：最后更新编码内容长度值到指定索引位（计算出的位置）。
     *
     * 特别注意：
     *  `JBoss Marshalling`自己就通过`ByteOutput`或`ByteInput`写入了值，如果使用`Google ProtoBuf`，则可以使用：
     *  `public abstract ByteBuf writeBytes(byte[] src, int srcIndex, int length);`这个方法写入到`ByteBuf`中。
     *
     * @param msg
     * @param out
     * @throws Exception
     */
    protected void encode(Object msg, ByteBuf out) throws Exception {
        try {
            // 获取写索引位置，占位
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER);

            // 序列化对象
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);

            // `Marshalling`序列化写入时，接收一个`ByteOutput`对象，需要覆盖`write`等方法，这里自定义了一个`ChannelBufferByteOutput`类
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();

            // 更新编码内容的长度（长度=当前写索引-起始索引-4位长度位）
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        } finally {
            marshaller.close();
        }
    }

}