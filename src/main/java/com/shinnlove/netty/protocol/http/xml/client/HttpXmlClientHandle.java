/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.http.xml.client;

import com.shinnlove.netty.protocol.http.xml.codec.HttpXmlRequest;
import com.shinnlove.netty.protocol.http.xml.codec.HttpXmlResponse;
import com.shinnlove.netty.protocol.http.xml.pojo.OrderFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author shinnlove.jinsheng
 * @version $Id: HttpXmlClientHandle.java, v 0.1 2018-06-29 下午12:00 shinnlove.jinsheng Exp $$
 */
public class HttpXmlClientHandle extends SimpleChannelInboundHandler<HttpXmlResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        HttpXmlRequest request = new HttpXmlRequest(null, OrderFactory.create(123));
        ctx.writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
        System.out.println("The client receive response of http header is : "
                           + msg.getHttpResponse().headers().names());
        System.out.println("The client receive response of http body is : " + msg.getResult());
    }
}
