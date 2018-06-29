/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.shinnlove.netty.protocol.netty.NettyConstant;
import com.shinnlove.netty.protocol.netty.codec.NettyMessageDecoder;
import com.shinnlove.netty.protocol.netty.codec.NettyMessageEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

/**
 * netty私有协议客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: NettyClient.java, v 0.1 2018-06-29 下午1:07 shinnlove.jinsheng Exp $$
 */
public class NettyClient {

    /** 有一个线程的定时调度线程池 */
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /** netty客户端线程组 */
    EventLoopGroup                   group    = new NioEventLoopGroup();

    /**
     * 向一个给定的IP地址套接字发起netty连接。
     * 
     * @param port
     * @param host
     * @throws Exception
     */
    public void connect(int port, String host) throws Exception {
        // 配置客户端NIO线程组
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // 私有消息解码器
                        ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                        // 私有消息编码器
                        ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
                        // netty自带读写超时处理器
                        ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
                        // 客户端登录验证处理器
                        ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
                        // 客户端心跳验证处理器
                        ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
                    }
                });

            // 发起异步连接操作
            ChannelFuture future = b.connect(new InetSocketAddress(host, port),
                new InetSocketAddress(NettyConstant.LOCAL_IP, NettyConstant.LOCAL_PORT)).sync();

            // 关闭通道
            future.channel().closeFuture().sync();

        } finally {
            // 所有资源释放完成之后，清空资源，再次发起重连操作
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        try {
                            // 发起重连操作，重连成功会阻塞在b.connect(...)函数处
                            connect(port, host);
                        } catch (Exception e) {
                            // 打印连接失败
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        // sleep被唤醒
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new NettyClient().connect(NettyConstant.REMOTE_PORT, NettyConstant.REMOTE_IP);
    }

}