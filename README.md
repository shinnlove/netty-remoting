
《Netty基础与源码分析》持续更新中...

# 一、Linux网络I/O模型

## 1、阻塞I/O模型

在进程空间中调用recvfrom，系统调用直到数据包到达被复制到应用进程缓冲区中或发生错误才返回，整个过程会一直等待。

## 2、非阻塞I/O模型

recvfrom从应用层到内核的时候，如果该缓冲区没有数据，直接返回`EWOULDBLOCK`错误，轮询检查这个状态，看是不是有数据到来。

## 3、I/O复用模型

Linux提供select/poll，进程通过一个或多个fd传递给select或poll系统调用，阻塞在select操作上。select/poll是顺序扫描fd是否就绪，而且支持的fd数量有限，使用受到一些制约。

Linux提供的另一个epoll系统调用使用基于事件驱动方式代替顺序扫描，因此性能更高。当有fd就绪时，立刻回调函数rollback。

## 4、信号驱动I/O模型

开启套接口信号驱动I/O功能，通过系统调用sigaction执行一个信号处理函数。当数据准备就绪时，就为该进程生成一个SIGIO信号，通过信号回调通知应用程序调用recvfrom来读取数据，并通知主循环函数处理数据。

## 5、异步I/O模型

告知内核某个I/O操作，并让内核在整个操作完成后通知。

# 二、Java4种类型的IO

## 1、BIO

## 2、伪异步IO

JDK`Stream`流读写阻塞。

## 3、NIO

## 4、AIO

## 5、4种IO对比

- 同步阻塞IO
- 异步非阻塞IO
- 多路复用器Selector
- 伪异步IO

4种不同的I/O类型对比表：

|               | 同步阻塞I/O（BIO） | 伪异步I/O      | 非阻塞I/O（NIO）           | 异步I/O（AIO）              |
| ------------- | ------------ | ----------- | --------------------- | ----------------------- |
| 客户端个数：I/O线程   | 1:1          | M:N（M可以大于N） | M:1（一个I/O线程处理多个客户端连接） | M:0（不需要启动额外的I/O线程，被动回调） |
| I/O类型（阻塞/非阻塞） | 阻塞IO         | 阻塞IO        | 非阻塞IO                 | 非阻塞IO                   |
| I/O类型（同步）     | 同步I/O        | 同步I/O       | 同步I/O（I/O多路复用）        | 异步I/O                   |
| API使用难度       | 简单           | 简单          | 非常复杂                  | 复杂                      |
| 调试难度          | 简单           | 简单          | 复杂                    | 复杂                      |
| 可靠性           | 非常差          | 差           | 高                     | 高                       |
| 吞吐量           | 低            | 中           | 高                     | 高                       |

可以看出NIO虽然性能比较高，但是编程难度较大。

## 6、Java原生NIO的弊端

- NIO类库和API复杂
- 需要具备其他技能——Java多线程，才能编写出高质量的NIO程序
- 可靠性的能力补齐、网络异常的工作量和难度都非常大
- JDK NIO的BUG——epoll bug会导致`Selector`空轮询，直至CPU100%

## 7、netty的优势

- API使用简单
- 功能强大，支持多种编解码和主流协议
- 定制能力强，通过`ChannelHandler`进行连环扩展；
- 性能高，与业界主流的NIO框架对比，综合性能最优
- 成熟、稳定，Netty修复了已经发现的所有JDK NIO BUG
- 社区活跃、版本迭代周期短
- 大规模的商业应用考验

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

### d) `ChannelHandlerContext`

这个类出现在每个Handler处理的形参中。来看它的接口定义：

```java
Enables a {@link ChannelHandler} to interact with its {@link ChannelPipeline}
 * and other handlers。
```

说明它就是为了让`pipeline`中多个`ChannelHandler`进行通信、传递IO事件或数据的上下文。

通常我们调用`ctx.write(xxx)`将信息异步写入通道中；调用`ctx.firexxx`将IO事件或异常传递给`pipeline`中下一个`ChannelHandler`；调用`ctx.flush()`将缓冲区的数据发送出去；调用`ctx.close()`释放通道占用的句柄和资源。

### e) 客户端连接

客户端只需要配置处理与服务端IO通信的线程组即可：

```java
// 客户端使用`NioSocketChannel`类来处理
b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
  .handler(new ChannelInitializer<SocketChannel>() {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
      // 在通道中加入时间处理器
      ChannelPipeline pipeline = ch.pipeline();
      pipeline.addLast(new TimeClientHandler());
    }
  });
```

当netty服务端就绪后，我们通常使用如下方法：

```java
ChannelFuture f = b.connect(host, port).sync();
```

来连接一个远程的netty服务端。连接后返回一个`ChannelFuture`对象，而后调用代码：

```java
f.channel().closeFuture().sync();
```

让`ChannelFuture`对象阻塞等待通道关闭事件。

### f) 客户端通道处理与服务端的不同

当作为客户端，通道与服务端连接建立成功后，通常是由客户端先发起`校验`、`业务请求`或`其他类型`数据包，以期待得到服务器的相应。所以一般在客户端的`ChannelHandler`中覆盖`channelActive()`方法来向激活的通道发送一些消息。

```java
@Override
public void channelActive(ChannelHandlerContext ctx) {
  // 直接写入缓存并即时输出
  ctx.writeAndFlush(firstMessage);
}
```

`ChannelHandler`在构造的时候就准备好一条消息（读写数据库或从缓存中），而后异步写入通道。

在netty服务端经过各种`ChannelHandler`处理后，总会有一个`ChannelHandler`把服务端的响应信息写入通道中，此时客户端的`channelRead()`方法被激活，可以在其中处理服务端发来的数据。

```java
@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
  ByteBuf buf = (ByteBuf) msg;
  byte[] req = new byte[buf.readableBytes()];
  buf.readBytes(req);
  String body = new String(req, "UTF-8");
  System.out.println("Now is : " + body);
}
```

这里就简单输出了服务端发来的信息。

### g) `ChannelInitializer`

通道初始化类也是一个`ChannelHandler`，它的继承结构是：

```java
/**
 * A special {@link ChannelHandler} which offers an easy way to initialize a {@link Channel} once it was
 * registered to its {@link EventLoop}.
 */
public abstract class ChannelInitializer<C extends Channel> extends ChannelHandlerAdapter
```

是`C)`中适配器的一个导出类，目的是为了在通道就绪后一次性添加多个`ChannelHandler`。它是一种特殊的`ChannelHandler`、只会初始化一次，提供了一种简单的方法将`ChannelHandler`关联到`EventLoop`中。

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



















