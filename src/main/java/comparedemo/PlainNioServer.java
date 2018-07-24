package comparedemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class PlainNioServer {

    /**
     * 向客户端返回echo, NIO,未使用Netty, 代码写起来很复杂
     * @param port
     * @throws IOException
     */
    public void serve(int port) throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));

        Selector selector = Selector.open();
        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        ByteBuffer echo = ByteBuffer.wrap("echo".getBytes());
        for(;;) {
            selector.select();//a block method
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();
                try{

                    if(next.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                        SocketChannel client = channel.accept();
                        client.configureBlocking(false);
                        client.register(selector,SelectionKey.OP_WRITE|SelectionKey.OP_READ,echo.duplicate());
                        System.out.println("accept msg from " + client);
                    }

                    if(next.isWritable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) next.channel();
                        SocketChannel client = channel.accept();

                        ByteBuffer buffer = (ByteBuffer) next.attachment();
                        while (buffer.hasRemaining()) {
                            if(client.write(buffer)==0) {
                                break;
                            }
                        }
                        client.close();
                    }
                }catch (IOException e) {
                    next.cancel();
                    next.channel().close();
                }
            }

        }
    }
}
