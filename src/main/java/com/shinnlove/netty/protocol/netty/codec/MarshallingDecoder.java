/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.codec;

import java.io.IOException;
import java.io.StreamCorruptedException;

import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import io.netty.buffer.ByteBuf;

/**
 * Marshalling反序列化解码器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: MarshallingDecoder.java, v 0.1 2018-06-29 下午1:09 shinnlove.jinsheng Exp $$
 */
public class MarshallingDecoder {

    /** Marshalling反序列化对象 */
    private final Unmarshaller unmarshaller;

    /**
     * 1GB的解码容量，超过则抛出`{@link StreamCorruptedException}`异常
     *
     * Creates a new decoder whose maximum object size is {@code 1048576} bytes.
     * If the size of the received object is greater than {@code 1048576} bytes,
     * a {@link StreamCorruptedException} will be raised.
     *
     * @throws IOException
     *
     */
    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
    }

    /**
     * 反序列化值。
     *
     * @param in
     * @return
     * @throws Exception
     */
    protected Object decode(ByteBuf in) throws Exception {
        // 读取字节缓存中下一个值的长度
        int objectSize = in.readInt();
        // 从字节缓存中切出读下表到值长度这么长的缓冲区
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        // 为`Marshalling`准备反序列化的字节输入
        ByteInput input = new ChannelBufferByteInput(buf);
        try {

            unmarshaller.start(input);
            // 将字节反序列化成`Object`类型
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();

            // 调整缓冲字节已读下标
            in.readerIndex(in.readerIndex() + objectSize);

            return obj;
        } finally {
            unmarshaller.close();
        }
    }

}