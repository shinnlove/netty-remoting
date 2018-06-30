/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * 使用UDP协议响应中国谚语查询的netty服务端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ChineseProverbServer.java, v 0.1 2018-06-29 下午1:21 shinnlove.jinsheng Exp $$
 */
public class ChineseProverbServer {

    public void run(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            // 使用`NioDatagramChannel`类
            b.group(group).channel(NioDatagramChannel.class)
            // 广播类型
                .option(ChannelOption.SO_BROADCAST, true)
                // 添加谚语查询服务端处理器
                .handler(new ChineseProverbServerHandler());

            // 等待得到一个通道关闭提示
            b.bind(port).sync().channel().closeFuture().await();
        } finally {
            // 优雅的关闭
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        new ChineseProverbServer().run(port);
    }

}