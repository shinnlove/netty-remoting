/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.nio;

/**
 * @author shinnlove.jinsheng
 * @version $Id: TimeClient.java, v 0.1 2018-06-29 下午1:38 shinnlove.jinsheng Exp $$
 */
public class TimeClient {

    /**
     * @param args
     */
    public static void main(String[] args) {

        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }

}