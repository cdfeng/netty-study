package comparedemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyOioServer {

    /**
     * 向客户端返回echo, BIO,使用Netty
     * @param port
     * @throws Exception
     */
    public void server(int port) throws Exception {

        OioEventLoopGroup eventExecutors = new OioEventLoopGroup();
        ByteBuf echo = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("echo", CharsetUtil.UTF_8));

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(eventExecutors)
                .channel(OioServerSocketChannel.class)
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
