/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.basic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 通道处理器适配器导出类——时间处理器，netty服务端ServerSocket的通道就绪后处理。
 *
 * 多个`ChannelHandlerAdapter`可以添加到netty通道的pipeline中，有序的执行。
 *
 * @author shinnlove.jinsheng
 * @version $Id: TimeServerHandler.java, v 0.1 2018-06-29 上午11:54 shinnlove.jinsheng Exp $$
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    /**
     * 读取通道就绪数据。
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
        System.out.println("The time server receive order : " + body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(
            System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        // 将数据写入缓冲区
        ctx.write(resp);
    }

    /**
     * 通道读取完毕。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将缓冲区的数据发送出去
        ctx.flush();
    }

    /**
     * 通道中发生异常。
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}