/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.file;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * `SimpleChannelInboundHandler`这个类的作用是???
 *
 * netty服务端文件服务器处理handler。
 *
 * @author shinnlove.jinsheng
 * @version $Id: FileServerHandler.java, v 0.1 2018-06-29 下午3:00 shinnlove.jinsheng Exp $$
 */
public class FileServerHandler extends SimpleChannelInboundHandler<String> {

    private static final String CR = System.getProperty("line.separator");

    /*
     * (non-Javadoc)
     *
     * @see
     * io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty
     * .channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        File file = new File(msg);
        if (file.exists()) {
            if (!file.isFile()) {
                ctx.writeAndFlush("Not a file : " + file + CR);
                return;
            }
            ctx.write(file + " " + file.length() + CR);

            // 以只读的方式打开文件
            RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r");
            // 直接将文件的内容写到通道里`FileRegion`
            FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0,
                randomAccessFile.length());

            // 写入内容、并输出换行符
            ctx.write(region);
            ctx.writeAndFlush(CR);

            randomAccessFile.close();
        } else {
            ctx.writeAndFlush("File not found: " + file + CR);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel
     * .ChannelHandlerContext, java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}