import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 服务端启动
 * 文件传输 version 0.1
 * 1、单服务端，支持多客户端同时连接连接（每一个连接独自开启子线程）
 * 2、支持断点续传
 * 3、客户端压缩后传输，服务端解压
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class Server {
    private static final Logger log = Logger.getLogger(Server.class.getName());
    /**
     * 服务端文件存储 path
     */
    public static final java.lang.String SERVER_DIR = "/data/webroot/";

    /**
     * 服务端端口
     */
    public static final int SERVER_PORT = 1111;

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
        serverSocketChannel.configureBlocking(false);

        while (true) {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel == null) {
                continue;
            }

            System.out.println(readString(socketChannel));
            sendString(socketChannel, "server send a message");
        }

    }

    private static void sendString(SocketChannel socketChannel, String str) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        socketChannel.write(buffer);
    }

    private static String readString(SocketChannel socketChannel) throws IOException {
        StringBuffer sb = new StringBuffer();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            sb.append((char) byteBuffer.get());
        }
        return sb.toString();
    }

}
