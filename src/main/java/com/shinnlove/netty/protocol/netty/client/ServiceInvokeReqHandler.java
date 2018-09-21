/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.client;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.shinnlove.netty.protocol.netty.MessageType;
import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端RPC服务请求发送与结果处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ServiceInvokeReqHandler.java, v 0.1 2018-09-21 上午11:41 shinnlove.jinsheng Exp $$
 */
public class ServiceInvokeReqHandler extends ChannelHandlerAdapter {

    /** 阻塞结果集队列 */
    private BlockingQueue<Object> resultQueue = new ArrayBlockingQueue<>(100);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("客户端开始接收处理结果");
        NettyMessage message = (NettyMessage) msg;

        if (message.getHeader() == null
            || message.getHeader().getType() == MessageType.SERVICE_RESP.value()) {
            // 处理心跳
            System.out.println("Receive server service invoke response message : ---> " + message);

            // 放入结果队列中
            Object result = message.getBody();
            System.out.println("将调用结果放入队列中" + result);
            resultQueue.offer(result);

        } else {
            // 透传其他消息
            ctx.fireChannelRead(msg);
        }
    }

}