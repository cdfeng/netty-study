package comparedemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyNioServer {


    /**
     * 向客户端返回echo, NIO,使用Netty，代码和NettyOioServer基本一样，只有1 2 两个地方不同
     * @param port
     * @throws Exception
     */
    public void server(int port) throws Exception {

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(); //1
        ByteBuf echo = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("echo", CharsetUtil.UTF_8));

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioServerSocketChannel.class)  //2
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ChannelFuture channelFuture = ctx.writeAndFlush(echo.duplicate());
                                channelFuture.addListener(ChannelFutureListener.CLOSE);
                            }
                        });
                    }
                });
        ChannelFuture channelFuture = bootstrap.bind().sync();
        channelFuture.channel().closeFuture().sync();
        eventExecutors.shutdownGracefully();
    }
}
