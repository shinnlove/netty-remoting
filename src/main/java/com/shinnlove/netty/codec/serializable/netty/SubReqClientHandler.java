/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.codec.serializable.netty;

import com.shinnlove.netty.codec.pojo.SubscribeReq;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author shinnlove.jinsheng
 * @version $Id: SubReqClientHandler.java, v 0.1 2018-06-29 下午1:54 shinnlove.jinsheng Exp $$
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    /**
     * Creates a client-side handler.
     */
    public SubReqClientHandler() {
    }

    /**
     * 客户端通道激活就生成对象并输出信息。
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    private SubscribeReq subReq(int i) {
        SubscribeReq req = new SubscribeReq();
        req.setAddress("南京市雨花台区软件大道101号华为基地");
        req.setPhoneNumber("138xxxxxxxxx");
        req.setProductName("Netty 最佳实践和原理分析");
        req.setSubReqID(i);
        req.setUserName("Lilinfeng");
        return req;
    }

    /**
     * 服务端回复消息时。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response : [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}