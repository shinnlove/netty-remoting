/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * 中国谚语客户端处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ChineseProverbClientHandler.java, v 0.1 2018-06-29 下午1:21 shinnlove.jinsheng Exp $$
 */
public class ChineseProverbClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    /**
     * 接收到服务端发来的消息。
     * 
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        // 直接就是utf-8的消息
        String response = msg.content().toString(CharsetUtil.UTF_8);
        if (response.startsWith("谚语查询结果: ")) {
            // 只处理目标消息
            System.out.println(response);
            ctx.close();
        }
    }

    /**
     * 发生异常时关闭连接。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}