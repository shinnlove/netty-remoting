/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.http.xml.codec;

import java.io.StringReader;
import java.nio.charset.Charset;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * 抽象HttpResponse->XML->Object
 *
 * @author shinnlove.jinsheng
 * @version $Id: AbstractHttpXmlDecoder.java, v 0.1 2018-06-29 下午12:01 shinnlove.jinsheng Exp $$
 */
public abstract class AbstractHttpXmlDecoder<T> extends MessageToMessageDecoder<T> {

    private IBindingFactory      factory;
    private StringReader         reader;
    /** 构造传入，XML与Class对象之间转换 */
    private Class<?>             clazz;
    private boolean              isPrint;
    private final static String  CHARSET_NAME = "UTF-8";
    private final static Charset UTF_8        = Charset.forName(CHARSET_NAME);

    protected AbstractHttpXmlDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    protected AbstractHttpXmlDecoder(Class<?> clazz, boolean isPrint) {
        this.clazz = clazz;
        this.isPrint = isPrint;
    }

    /**
     * 使用Jibx工厂类创建`IUnmarshallingContext`上下文，解码XML和对象。
     *
     * @param arg0
     * @param body          ByteBuf来自DefaultFullHttpResponse.toContent()得到的字节缓冲数组。
     * @return
     * @throws Exception
     */
    protected Object decode0(ChannelHandlerContext arg0, ByteBuf body) throws Exception {
        factory = BindingDirectory.getFactory(clazz);
        String content = body.toString(UTF_8);
        if (isPrint) {
            System.out.println("The body is : " + content);
        }
        reader = new StringReader(content);
        IUnmarshallingContext uctx = factory.createUnmarshallingContext();
        Object result = uctx.unmarshalDocument(reader);
        reader.close();
        reader = null;
        return result;
    }

    /**
     * Calls {@link ChannelHandlerContext#fireExceptionCaught(Throwable)} to
     * forward to the next {@link ChannelHandler} in the {@link ChannelPipeline}
     * .
     *
     * Sub-classes may override this method to change behavior.
     */
    @Skip
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 释放资源
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }

}
