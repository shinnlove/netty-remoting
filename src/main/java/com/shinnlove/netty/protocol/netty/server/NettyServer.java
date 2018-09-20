/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.server;

import java.io.IOException;

import com.shinnlove.netty.protocol.netty.NettyConstant;
import com.shinnlove.netty.protocol.netty.codec.NettyMessageDecoder;
import com.shinnlove.netty.protocol.netty.codec.NettyMessageEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * Netty私有协议服务端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: NettyServer.java, v 0.1 2018-06-29 下午1:12 shinnlove.jinsheng Exp $$
 */
public class NettyServer {

    public void bind() {
        // 配置服务端的NIO线程组
        // 服务端需要两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws IOException {
                    // Tips：netty的每个handler的`channelRead`和exceptionCaught`如果调用`fire...`可以发送给下个handler处理!!
                    ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                    ch.pipeline().addLast(new NettyMessageEncoder());
                    // ❤心跳超时直接采用Netty的ReadTimeoutHandler机制，当一定周期内（默认50秒）没有收到对方任何消息，主动关闭链路
                    ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                    // 服务端登录验证响应处理器(名称随意，默认使用类名)
                    ch.pipeline().addLast(new LoginAuthRespHandler());
                    // 服务端心跳响应处理器(服务端是pong回去心跳)
                    ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRespHandler());
                }
            });

        // 绑定端口，同步等待成功
        try {
            ChannelFuture f = b.bind(NettyConstant.REMOTE_IP, NettyConstant.REMOTE_PORT).sync();

            System.out.println("Netty server start ok : "
                               + (NettyConstant.REMOTE_IP + ":" + NettyConstant.REMOTE_PORT));

            Thread.sleep(100000);

            // 等待服务端监听端口关闭(服务端代码会阻塞在这里，直到得到一个close的future结果)
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            System.out.println("遇到了`InterruptedException`错误");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("遇到了`Exception`错误");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        new NettyServer().bind();
    }

}