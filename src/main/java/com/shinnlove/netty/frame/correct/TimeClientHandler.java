/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.frame.correct;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * netty时间客户端处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: TimeClientHandler.java, v 0.1 2018-06-29 下午1:33 shinnlove.jinsheng Exp $$
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());

    private int                 counter;

    private byte[]              req;

    /**
     * Creates a client-side handler.
     */
    public TimeClientHandler() {
        // 构造的时候准备好信息
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    /**
     * 与服务端通道连接后就直接输出数据，输出100次。
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf message = null;
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    /**
     * 服务端通道有响应后，输出服务端的响应。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("Now is : " + body + " ; the counter is : " + ++counter);
    }

    /**
     * 异常发生后。
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 释放资源
        logger.warning("Unexpected exception from downstream : " + cause.getMessage());
        ctx.close();
    }

}