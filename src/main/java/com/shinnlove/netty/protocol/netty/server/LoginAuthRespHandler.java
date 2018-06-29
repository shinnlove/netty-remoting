/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.shinnlove.netty.protocol.netty.MessageType;
import com.shinnlove.netty.protocol.netty.struct.Header;
import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * netty服务端登录验证响应处理器。
 *
 * @author shinnlove.jinsheng
 * @version $Id: LoginAuthRespHandler.java, v 0.1 2018-06-29 下午1:12 shinnlove.jinsheng Exp $$
 */
public class LoginAuthRespHandler extends ChannelHandlerAdapter {

    /** key->IP, value->true代表登录 */
    private Map<String, Boolean> nodeCheck  = new ConcurrentHashMap<String, Boolean>();
    /** 可登录白名单列表 */
    private String[]             whiteList = { "127.0.0.1", "192.168.1.104" };

    /**
     * 当服务端通道可读的时候，通常是收到了客户端的消息。
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        // 如果是握手请求消息，处理，其它消息透传
        if (message.getHeader() != null
            && message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
            // 来自客户端的登录请求消息

            // 上下文中能拿到客户端地址
            String nodeIndex = ctx.channel().remoteAddress().toString();
            NettyMessage loginResp = null;

            if (nodeCheck.containsKey(nodeIndex)) {
                // 重复登陆，拒绝
                loginResp = buildLoginAuthResponse((byte) -1);
            } else {
                // 第一次登录——检测是否在白名单中(这里可以操作数据库或者打印日志方便监控统计)
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                String ip = address.getAddress().getHostAddress();

                // 默认不成功、error code是-1
                boolean isOK = false;
                int result = -1;
                for (String WIP : whiteList) {
                    if (WIP.equals(ip)) {
                        isOK = true;
                        break;
                    }
                }

                if (isOK) {
                    result = 0;
                    // 登录成功就放入session映射中
                    nodeCheck.put(nodeIndex, true);
                }

                // 生成响应
                loginResp = buildLoginAuthResponse((byte) result);
            }

            System.out.println("The login response is : " + loginResp + " body ["
                               + loginResp.getBody() + "]");
            ctx.writeAndFlush(loginResp);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 生成服务端登录验证响应。
     *
     * @param result 0是OK、-1是失败。
     * @return
     */
    private NettyMessage buildLoginAuthResponse(byte result) {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        // 登录响应类型
        header.setType(MessageType.LOGIN_RESP.value());
        message.setHeader(header);
        message.setBody(result);
        return message;
    }

    /**
     * 当登录请求发生错误的时候。
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 特别注意：通道发生错误的时候，要把登录的sessionId释放掉!
        nodeCheck.remove(ctx.channel().remoteAddress().toString());// 删除缓存
        ctx.close();

        // 透传异常
        ctx.fireExceptionCaught(cause);
    }

}