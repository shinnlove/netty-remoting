/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * 文件处理服务器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: FileServer.java, v 0.1 2018-06-29 下午3:00 shinnlove.jinsheng Exp $$
 */
public class FileServer {

    public void run(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    /*
                     * (non-Javadoc)
                     *
                     * @see
                     * io.netty.channel.ChannelInitializer#initChannel(io
                     * .netty.channel.Channel)
                     */
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // 字符串encoder、行分隔符、字符串decoder、最后是文件服务器处理类
                        ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8),
                            new LineBasedFrameDecoder(1024), new StringDecoder(CharsetUtil.UTF_8),
                            new FileServerHandler());
                    }
                });

            ChannelFuture f = b.bind(port).sync();
            System.out.println("Start file server at port : " + port);
            f.channel().closeFuture().sync();
        } finally {
            // 优雅停机
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
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
        new FileServer().run(port);
    }

}