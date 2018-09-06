/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.codec.protobuf;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 客户端用以生成`ProtoBuf`支持序列化的对象的`Handler`。
 *
 * @author shinnlove.jinsheng
 * @version $Id: SubReqClientHandler.java, v 0.1 2018-06-29 下午1:49 shinnlove.jinsheng Exp $$
 */
public class SubReqClientHandler extends ChannelHandlerAdapter {

    /**
     * Creates a client-side handler.
     */
    public SubReqClientHandler() {
    }

    /**
     * 客户端：通道被激活的时候产生10个对象并且写到缓冲区并发送。
     *
     * 有`ProtuBuf`的`Encoder`会将写入的消息用`ProtoBuf`的加密方式序列化出去。
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 0; i < 10; i++) {
            ctx.write(subReq(i));
        }
        ctx.flush();
    }

    /**
     * 生成`ProtoBuf`支持序列化的对象。
     *
     * @param i
     * @return
     */
    private SubscribeReqProto.SubscribeReq subReq(int i) {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq
            .newBuilder();
        builder.setSubReqID(i);
        builder.setUserName("Lilinfeng");
        builder.setProductName("Netty Book For Protobuf");
        List<String> address = new ArrayList<>();
        address.add("NanJing YuHuaTai");
        address.add("BeiJing LiuLiChang");
        address.add("ShenZhen HongShuLin");
        builder.addAllAddress(address);
        return builder.build();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response : [" + msg + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}