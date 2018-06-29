/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.frame.fault;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * netty时间客户端处理。
 *
 * @author shinnlove.jinsheng
 * @version $Id: TimeClientHandler.java, v 0.1 2018-06-29 下午1:36 shinnlove.jinsheng Exp $$
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());

    private int                 counter;

    private byte[]              req;

    /**
     * Creates a client-side handler.
     */
    public TimeClientHandler() {
        // 构造函数中准备发送的消息
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
    }

    /**
     * 客户端在通道激活后，直接发送消息：QUERY TIME ORDER\n。
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
     * 客户端接受服务端消息、通道可读时。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        // 输出客户端的消息
        System.out.println("Now is : " + body + " ; the counter is : " + ++counter);
    }

    /**
     * 发生异常的处理。
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