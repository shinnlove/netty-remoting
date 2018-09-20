/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.server;

import com.shinnlove.netty.protocol.netty.MessageType;
import com.shinnlove.netty.protocol.netty.struct.Header;
import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * netty服务端心跳响应处理器。
 *
 * 采用ping-pong机制的心跳。
 *
 * @author shinnlove.jinsheng
 * @version $Id: HeartBeatRespHandler.java, v 0.1 2018-06-29 下午1:11 shinnlove.jinsheng Exp $$
 */
public class HeartBeatRespHandler extends ChannelHandlerAdapter {

    /**
     * 每当通道可读时的处理。
     *
     * 在客户端发送消息后，在类型是`MessageType.HEARTBEAT_REQ`时，本处理器进行
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端开始处理心跳消息");
        NettyMessage message = (NettyMessage) msg;

        if (message.getHeader() == null
            || message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
            // 处理心跳
            System.out.println("Receive client heart beat message : ---> " + message);

            // 生成心跳响应包
            NettyMessage heartBeat = buildHeatBeatResp();
            System.out.println("Send heart beat response message to client : ---> " + heartBeat);

            // 发送
            ctx.writeAndFlush(heartBeat);
        } else {
            // 透传其他消息
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 生成一个服务端的心跳响应包。
     * 
     * @return
     */
    private NettyMessage buildHeatBeatResp() {
        NettyMessage message = new NettyMessage();

        // 空header、心跳响应类型
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.value());

        message.setHeader(header);

        return message;
    }

}