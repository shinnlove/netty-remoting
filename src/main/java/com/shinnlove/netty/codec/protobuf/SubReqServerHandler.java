/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.codec.protobuf;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * `ProtoBuf`方式处理消息的服务端处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: SubReqServerHandler.java, v 0.1 2018-06-29 下午1:49 shinnlove.jinsheng Exp $$
 */
@Sharable
public class SubReqServerHandler extends ChannelHandlerAdapter {

    /**
     * 通道可读的情况下，即客户端发来消息时，服务端如果发现请求对象用户名是目标用户，则发出响应。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
        if ("Lilinfeng".equalsIgnoreCase(req.getUserName())) {
            System.out.println("Service accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    /**
     * 生成一个简单的`ProtoBuf`支持的可序列化对象。
     *
     * @param subReqID
     * @return
     */
    private SubscribeRespProto.SubscribeResp resp(int subReqID) {
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp
            .newBuilder();
        builder.setSubReqID(subReqID);
        builder.setRespCode(0);
        builder.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();// 发生异常，关闭链路
    }

}