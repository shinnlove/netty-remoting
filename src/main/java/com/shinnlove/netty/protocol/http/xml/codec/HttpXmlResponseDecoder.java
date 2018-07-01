/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.http.xml.codec;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

/**
 * HttpXml响应解码器，持有`DefaultFullHttpResponse`对象。
 *
 * @author shinnlove.jinsheng
 * @version $Id: HttpXmlResponseDecoder.java, v 0.1 2018-06-29 下午12:03 shinnlove.jinsheng Exp $$
 */
public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {

    public HttpXmlResponseDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    /**
     * 在构造的时候传入Class<?> clazz类型。
     *
     * @param clazz
     * @param isPrintlog
     */
    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrintlog) {
        super(clazz, isPrintlog);
    }

    /**
     * 覆盖`MessageToMessageDecoder`中的抽象方法，解码的时候会被调到。
     *
     * @see MessageToMessageDecoder#decode(ChannelHandlerContext, Object, List) 
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> out)
                                                                                                   throws Exception {
        // 调用抽象类的decode0(将XML字节流转成Object对象，再塞入`HttpXmlResponse`中)
        HttpXmlResponse resHttpXmlResponse = new HttpXmlResponse(msg, decode0(ctx, msg.content()));
        out.add(resHttpXmlResponse);
    }

}
