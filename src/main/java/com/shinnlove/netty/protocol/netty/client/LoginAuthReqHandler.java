/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.client;

import com.shinnlove.netty.protocol.netty.MessageType;
import com.shinnlove.netty.protocol.netty.struct.Header;
import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端——登录验证请求处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: LoginAuthReqHandler.java, v 0.1 2018-06-29 下午1:07 shinnlove.jinsheng Exp $$
 */
public class LoginAuthReqHandler extends ChannelHandlerAdapter {

    /**
     * 当客户端和服务端通道连接成功后。
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // netty通道连接成功后，客户端立刻发送一个登录请求包给服务端
        System.out.println("客户端发送登录请求");
        ctx.writeAndFlush(buildLoginReq());
    }

    /**
     * 当客户端通道可读取时、接收到了服务端的消息。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端开始处理消息");
        NettyMessage message = (NettyMessage) msg;

        // 如果是握手应答消息，需要判断是否认证成功
        if (message.getHeader() != null
            && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            // 如果是登录验证响应、判断是否登录成功
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0) {
                // 握手失败，关闭连接
                ctx.close();
            } else {
                // `error code != 0` 说明登录成功
                System.out.println("Login is ok : " + message);

                // 特别注意：为何再透传消息？因为要给心跳处理器发起定时任务。如果这里不发送而把消息处理掉了，心跳处理器是不会有`channelRead`的处理的!!
                ctx.fireChannelRead(msg);
            }
        } else {
            // 透传非登录验证类型消息
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 生成登录请求报文。
     *
     * @return
     */
    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        // 客户端登录请求类型
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

}