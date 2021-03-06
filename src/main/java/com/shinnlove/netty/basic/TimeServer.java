/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.basic;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty服务端基本用法。
 *
 * @author shinnlove.jinsheng
 * @version $Id: TimeServer.java, v 0.1 2018-06-29 上午11:53 shinnlove.jinsheng Exp $$
 */
public class TimeServer {

    /**
     * 将netty服务端启动并且绑定在某个端口上监听。
     *
     * @param port
     * @throws Exception
     */
    public void bind(int port) throws Exception {
        // 配置服务端的NIO线程组
        // 一个NIO线程组用来接收客户端连接、一个NIO线程组用来进行`SocketChannel`读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 配置启动类
            ServerBootstrap b = new ServerBootstrap();
            // 服务端使用`NioServerSocketChannel`这个类
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 获取端口通道的pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new TimeServerHandler());
                    }
                });
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭(服务端代码会阻塞在这里，直到得到一个close的future结果)
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new TimeServer().bind(port);
    }

}