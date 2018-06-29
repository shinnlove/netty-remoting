/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.pio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.shinnlove.netty.bio.TimeServerHandler;

/**
 * @author shinnlove.jinsheng
 * @version $Id: TimeServer.java, v 0.1 2018-06-29 下午1:29 shinnlove.jinsheng Exp $$
 */
public class TimeServer {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {

            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }

        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            TimeServerHandlerExecutePool singleExecutor = new TimeServerHandlerExecutePool(50,
                10000);// 创建IO任务线程池
            while (true) {
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }

}