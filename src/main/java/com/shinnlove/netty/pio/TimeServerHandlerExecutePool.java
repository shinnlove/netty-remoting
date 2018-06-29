/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.netty.pio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shinnlove.jinsheng
 * @version $Id: TimeServerHandlerExecutePool.java, v 0.1 2018-06-29 下午1:29 shinnlove.jinsheng Exp $$
 */
public class TimeServerHandlerExecutePool {

    private ExecutorService executor;

    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize,
            120L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize));
    }

    public void execute(java.lang.Runnable task) {
        executor.execute(task);
    }

}