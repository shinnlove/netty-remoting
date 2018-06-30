/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.udp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;

/**
 * 中国谚语查询netty服务端处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ChineseProverbServerHandler.java, v 0.1 2018-06-29 下午1:22 shinnlove.jinsheng Exp $$
 */
public class ChineseProverbServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    // 谚语列表
    private static final String[] DICTIONARY = { "只要功夫深，铁棒磨成针。", "旧时王谢堂前燕，飞入寻常百姓家。",
            "洛阳亲友如相问，一片冰心在玉壶。", "一寸光阴一寸金，寸金难买寸光阴。", "老骥伏枥，志在千里。烈士暮年，壮心不已!" };

    private String nextQuote() {
        int quoteId = ThreadLocalRandom.current().nextInt(DICTIONARY.length);
        return DICTIONARY[quoteId];
    }

    /**
     * 接收到客户端发来的消息时。
     *
     * 特别注意：通道写入使用`DatagramPacket`类。
     *
     * @param ctx
     * @param packet
     * @throws Exception
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String req = packet.content().toString(CharsetUtil.UTF_8);
        System.out.println(req);
        if ("谚语字典查询?".equals(req)) {
            // 将信息以utf-8格式写入`ByteBuf`中，再用UDP的`DatagramPacket`包装写入通道中。
            String respInfo = "谚语查询结果: " + nextQuote();
            ByteBuf data = Unpooled.copiedBuffer(respInfo, CharsetUtil.UTF_8);
            DatagramPacket resp = new DatagramPacket(data, packet.sender());
            ctx.writeAndFlush(resp);
        }
    }

    /**
     * 发生异常时关闭通道。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

}