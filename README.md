
《Netty基础与源码分析》持续更新中...

# 一、Java IO

1、epoll与select的区别

# 二、4种类型的基础IO

## 1、BIO

## 2、伪异步IO

JDK`Stream`流读写阻塞。

## 3、NIO

## 4、AIO

## 5、4种IO对比

## 6、Java原生NIO的弊端

## 7、netty的优势

# 三、netty使用

## 1、基础使用

### a) 线程组

netty服务端通常配置两个线程组，一个NIO线程组用来接收客户端连接、一个NIO线程组用来进行`SocketChannel`读写。

```java
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
        // 配置启动类
        ServerBootstrap b = new ServerBootstrap();
        // 服务端使用`NioServerSocketChannel`这个类
        b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
```

### b) 端口绑定与关闭

调用同步阻塞方法sync等待绑定才做完成，完成后会返回一个`ChannelFuture`。
```java
    ChannelFuture f = b.bind(port).sync();
```
而后调用`ChannelFuture`通道的关闭同步等待方法进行阻塞，等待服务端链路关闭。
```java
    f.channel().closeFuture().sync();
```

### c) `ChannelHandlerAdapter`
名词解释：`ChannelPipeline`是一个通道的管道，在这个管道上有很多个处理器`ChannelHandler`。
每当客户端与服务端的连接通道发生IO事件，就会依次执行`pipeline`上挂载的每个`ChannelHandler`。
`ChannelHandlerAdapter`是`ChannelHandler`的一个单例实现。

例如在netty服务端增加通道：
```java
    b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 获取端口通道的pipeline
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new TimeServerHandler());
            }
        });
```
其中`TimeServerHandler`是管道上的一个处理器，它覆盖了超类的3个方法：`channelRead`、`channelReadComplete`和`exceptionCaught`。
```java
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将数据写入缓冲区
        ctx.write("hello");
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将缓冲区的数据发送出去
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
```
注意到这三个方法在超类中有实现：
```java
    /**
     * Calls {@link ChannelHandlerContext#fireExceptionCaught(Throwable)} to forward
     * to the next {@link ChannelHandler} in the {@link ChannelPipeline}.
     *
     * Sub-classes may override this method to change behavior.
     */
    @Skip
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
```

- i) 打了`@Skip`注解
- ii) 默认实现是`ctx.fireExceptionCaught(cause);`
- iii) 子类可以覆盖这个默认的方法，但如果调用`上下文#fireXXX`方法，可以将错误传递给`ChannelPipeLine`的下一个`ChannelHandler`去捕捉。**直到有一个`ChannelHandler`覆盖`exceptionCaught`方法后，可以在内部处理这个错误。一旦某个`ChannelHandler`不再调用`fireXXX`方法，则整个错误IO事件在管道中的传递就终止了。**

因此普通的netty处理器handler的实现就是继承`ChannelHandlerAdapter`类，覆盖其中处理的方法。

通常这些钩子有：

- `channelRegistered`——当通道注册后
- `channelActive`——当通道激活后
- `channelRead`——当客户端/服务端写入数据被接收到时（通常做出响应并回写信息）
- `channelReadComplete`——当通道数据读完后（通常在read时会写入信息，在读完后就flush出去）
- `exceptionCaught`——当通道发生IO异常后

这些钩子都可以在方法签名中找到如何使用上下文`ChannelHandlerContext`来`fire`传递IO事件到下一个处理器。


## 2、TCP粘包/拆包

# 四、编解码技术

## 1、MessagePack

## 2、Google Protobuf

## 3、JBoss Marshalling

# 五、netty与协议

## 1、HTTP协议与XML

## 2、WebSocket

## 3、私有协议栈

# 六、netty客户端、服务端创建与启动


# 七、Netty源代码解读

## 1、ByteBuf和相关辅助类

## 2、Channel和Unsafe


## 3、ChannelPipeline和ChannelHandler


## 4、EventLoop和EventLoopGroup


## 5、Future和Promise


# 八、Netty架构

## 1、多线程在Netty中的应用


## 2、高性能之道


## 3、可靠性


## 4、安全性





















