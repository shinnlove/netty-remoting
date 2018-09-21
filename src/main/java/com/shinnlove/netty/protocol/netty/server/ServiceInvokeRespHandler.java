/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.protocol.netty.server;

import java.util.HashMap;
import java.util.Map;

import com.shinnlove.netty.protocol.netty.MessageType;
import com.shinnlove.netty.protocol.netty.struct.Header;
import com.shinnlove.netty.protocol.netty.struct.NettyMessage;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 服务端远程RPC服务调用请求处理。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ServiceInvokeRespHandler.java, v 0.1 2018-09-21 上午11:25 shinnlove.jinsheng Exp $$
 */
public class ServiceInvokeRespHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端开始处理服务调用");
        NettyMessage message = (NettyMessage) msg;

        if (message.getHeader() == null
            || message.getHeader().getType() == MessageType.SERVICE_REQ.value()) {
            // 处理心跳
            System.out.println("Receive client service invoke request message : ---> " + message);

            // 反射寻找服务，把调用结果送回去
            // 这里下面部分改成从spring上下文获取
            String fullClassName = "com.shinnlove.springall.service.UserQueryService";
            String methodName = "getUserById";
            Map<String, Object> params = new HashMap<>();
            params.put("id", 1);

            // 调用服务(此处应该有try...catch块处理)
            NettyMessage resp = serviceInvoke(fullClassName, methodName, params);

            // 发送
            ctx.writeAndFlush(resp);
        } else {
            // 透传其他消息
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 组装服务调用结果。
     *
     * @param clazz  
     * @param method
     * @param params
     * @return
     * @throws Exception
     */
    private NettyMessage serviceInvoke(String clazz, String method, Map<String, Object> params)
                                                                                               throws Exception {
        NettyMessage response = new NettyMessage();
        Header header = new Header();
        // 登录响应类型
        header.setType(MessageType.SERVICE_RESP.value());
        response.setHeader(header);
        response.setBody("Service Invoke OK!");
        return response;
    }

}