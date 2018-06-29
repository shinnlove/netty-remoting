/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.http.xml.codec;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;

/**
 * @author shinnlove.jinsheng
 * @version $Id: HttpXmlResponseDecoder.java, v 0.1 2018-06-29 下午12:03 shinnlove.jinsheng Exp $$
 */
public class HttpXmlResponseDecoder extends AbstractHttpXmlDecoder<DefaultFullHttpResponse> {

    public HttpXmlResponseDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    public HttpXmlResponseDecoder(Class<?> clazz, boolean isPrintlog) {
        super(clazz, isPrintlog);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, DefaultFullHttpResponse msg, List<Object> out)
                                                                                                   throws Exception {
        HttpXmlResponse resHttpXmlResponse = new HttpXmlResponse(msg, decode0(ctx, msg.content()));
        out.add(resHttpXmlResponse);
    }

}
