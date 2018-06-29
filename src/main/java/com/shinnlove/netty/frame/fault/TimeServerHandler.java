/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.frame.fault;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * netty服务端时间处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: TimeServerHandler.java, v 0.1 2018-06-29 下午1:37 shinnlove.jinsheng Exp $$
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    /**
     * 服务端通道可读。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 消息读入字节缓冲区
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);

        // 转成utf-8字符(去掉换行符)
        String body = new String(req, "UTF-8").substring(0,
            req.length - System.getProperty("line.separator").length());

        System.out.println("The time server receive order : " + body + " ; the counter is : "
                           + ++counter);

        // 必须得到指定的字符串"QUERY TIME ORDER"才输出时间
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(
            System.currentTimeMillis()).toString() : "BAD ORDER";

        // 将时间信息转成字节数组
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());

        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}