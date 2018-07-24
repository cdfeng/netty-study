package echoserverdemo.client;

import echoserverdemo.server.EchoServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class EchoClient {

    private String host;
    private int port;

    private EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(host,port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new EchoClientHandler());
                    }
                });

        try {
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        String host = "localhost";
        int port = 8084;
        if(args.length>=2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        EchoClient echoClient = new EchoClient(host, port);
        echoClient.start();

        //也可以用一下老方法socket编程
//        connectUseOldMethod();
    }

    private static void connectUseOldMethod() throws IOException {

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost",8084));
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("hello world".getBytes());
        outputStream.flush();
        InputStream inputStream = socket.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s = bufferedReader.readLine();
        System.out.println(s);
        bufferedReader.close();
    }
}
