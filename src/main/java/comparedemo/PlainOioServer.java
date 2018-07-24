package comparedemo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PlainOioServer {

    /**
     * 向客户端返回echo, BIO
     * @param port
     * @throws IOException
     */
    public void serve(int port) throws IOException {

        ServerSocket serverSocket = new ServerSocket(port);

        for (;;) {
            Socket socket = serverSocket.accept();
            OutputStream outputStream = socket.getOutputStream();
            new Thread(()->{
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                try {
                    writer.write("echo");
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
