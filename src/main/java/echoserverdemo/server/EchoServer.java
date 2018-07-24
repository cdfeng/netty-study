package echoserverdemo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {

    private int port;

    private EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {

        int port = 8084;

        if(args.length >=1) {
            port = Integer.parseInt(args[0]);
        }

        new EchoServer(port).start();
    }

    private void start() {

        final EchoServerHandler echoServerHandler = new EchoServerHandler();

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(echoServerHandler);
                    }
                });
            ChannelFuture channelFuture= serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            System.out.println("echo server closed!");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
