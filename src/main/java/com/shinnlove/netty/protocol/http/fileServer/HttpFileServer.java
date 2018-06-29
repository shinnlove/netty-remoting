/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.http.fileServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * `HttpRequestDecoder`、`HttpObjectAggregator`、`HttpResponseEncoder`、`ChunkedWriteHandler`四个http类是干嘛的??
 *
 * 具体处理http file的处理器——`HttpFileServerHandler`。
 *
 * @author shinnlove.jinsheng
 * @version $Id: HttpFileServer.java, v 0.1 2018-06-28 下午7:34 shinnlove.jinsheng Exp $$
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "/src/main/java/com/shinnlove/netty/";

    private static final String IP_ADDRESS  = "127.0.0.1";

    public void run(final int port, final String url) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // `HttpRequestDecoder`解码器在每个HTTP消息中会生成多个消息对象
                        ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                        // 将多个消息转换为单一的`FullHttpRequest`或者`FullHttpResponse`
                        ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                        ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                        // `ChunkedWriteHandler`支持异步发送大的码流(大的文件传输)、但不会占用过多内存，防止Java内存溢出
                        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                        // 文件服务器业务处理逻辑handler
                        ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(url));
                    }
                });
            // 绑定在端口上同步等待调用
            ChannelFuture future = b.bind(IP_ADDRESS, port).sync();
            System.out.println("HTTP文件目录服务器启动，网址是 : " + "http://" + IP_ADDRESS + ":" + port + url);
            future.channel().closeFuture().sync();
        } finally {
            // 优雅关闭netty
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 约定第一个参数是端口号，第二个参数是文件路径。
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String url = DEFAULT_URL;
        if (args.length > 1)
            url = args[1];
        new HttpFileServer().run(port, url);
    }

}