import sun.swing.StringUIClientPropertyKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 客户端启动
 * 可多线程上传多个文件
 * @author xiongqiqmeng
 * @since 2019/12/20 16:33
 */
public class Client {
    /**
     * 服务端ip
     */
    public static final String SERVER_HOST = "127.0.0.1";
    /**
     * 服务端端口
     */
    public static final int SERVER_POST = 1111;

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_POST));

        sendString(socketChannel, "just a simple nio text message");
        System.out.println(readString(socketChannel));
        socketChannel.close();
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
