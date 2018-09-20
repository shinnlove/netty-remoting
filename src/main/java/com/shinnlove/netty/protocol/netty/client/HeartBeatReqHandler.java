/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.client;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.shinnlove.netty.protocol.netty.MessageType;
import com.shinnlove.netty.protocol.netty.struct.Header;
import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端心跳请求处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: HeartBeatReqHandler.java, v 0.1 2018-06-29 下午1:06 shinnlove.jinsheng Exp $$
 */
public class HeartBeatReqHandler extends ChannelHandlerAdapter {

    /** 心跳结果Future */
    private volatile ScheduledFuture<?> heartBeat;

    /**
     * 当客户端接收到服务端发来的消息时。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端发送心跳与处理心跳响应");
        NettyMessage message = (NettyMessage) msg;

        // 握手成功，主动发送心跳消息
        if (message.getHeader() != null
            && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            // 如果收到的消息是服务端的登录验证响应

            // 使用上下文`EventExecutor`提交一个5秒一次的定时任务：生成客户端心跳包
            // heartBeat是一个定时future，当发生异常时可以被取消(!!!特别用来在服务端宕机时期、客户端不再发送心跳)
            heartBeat = ctx.executor().scheduleAtFixedRate(
                new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);

        } else if (message.getHeader() != null
                   && message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
            // 如果收到的消息是服务端的心跳响应

            System.out.println("Client receive server heart beat message : ---> " + message);

        } else {
            // 透传其他消息
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 定时发送心跳包任务。
     */
    private class HeartBeatTask implements Runnable {

        /** 不可变的通道处理上下文 */
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage heatBeat = buildHeatBeatReq();
            System.out.println("Client send heart beat message to server : ---> " + heatBeat);
            ctx.writeAndFlush(heatBeat);
        }

        /**
         * 生成一个客户端的心跳请求。
         *
         * @return
         */
        private NettyMessage buildHeatBeatReq() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(MessageType.HEARTBEAT_REQ.value());
            message.setHeader(header);
            return message;
        }

    }

    /**
     * 客户端netty通道发生异常时释放资源。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 当客户端发现与服务端通道之间出现异常时、先让心跳发送定时任务停止、而后再将错误扩散出去
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }

}